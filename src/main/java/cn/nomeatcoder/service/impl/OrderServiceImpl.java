package cn.nomeatcoder.service.impl;

import cn.nomeatcoder.common.Const;
import cn.nomeatcoder.common.PageInfo;
import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.domain.*;
import cn.nomeatcoder.common.query.*;
import cn.nomeatcoder.common.vo.OrderItemVo;
import cn.nomeatcoder.common.vo.OrderProductVo;
import cn.nomeatcoder.common.vo.OrderVo;
import cn.nomeatcoder.common.vo.ShippingVo;
import cn.nomeatcoder.dal.mapper.*;
import cn.nomeatcoder.service.OrderService;
import cn.nomeatcoder.utils.BigDecimalUtils;
import cn.nomeatcoder.utils.FTPUtils;
import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

@Slf4j
@Service("orderService")
public class OrderServiceImpl implements OrderService {

	@Resource
	private OrderMapper orderMapper;

	@Resource
	private OrderItemMapper orderItemMapper;

	@Resource
	private PayInfoMapper payInfoMapper;

	@Resource
	private CartMapper cartMapper;

	@Resource
	private ProductMapper productMapper;

	@Resource
	private ShippingMapper shippingMapper;

	private static AlipayTradeService tradeService;

	static {

		/** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
		 *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
		 */
		Configs.init("zfbinfo.properties");

		/** 使用Configs提供的默认参数
		 *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
		 */
		tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public ServerResponse createOrder(Integer userId, Integer shippingId) {

		//从购物车中获取数据
		CartQuery query = new CartQuery();
		query.setUserId(userId);
		List<Cart> cartList = cartMapper.find(query);

		//计算这个订单的总价
		ServerResponse serverResponse = this.getCartOrderItem(userId, cartList);
		if (!serverResponse.isSuccess()) {
			return serverResponse;
		}
		List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
		BigDecimal payment = this.getOrderTotalPrice(orderItemList);


		//生成订单
		Order order = this.assembleOrder(userId, shippingId, payment);
		if (order == null) {
			return ServerResponse.error("生成订单错误");
		}
		if (CollectionUtils.isEmpty(orderItemList)) {
			return ServerResponse.error("购物车为空");
		}
		for (OrderItem orderItem : orderItemList) {
			orderItem.setOrderNo(order.getOrderNo());
		}
		//mybatis 批量插入
		orderItemMapper.batchInsert(orderItemList);

		//生成成功,我们要减少我们产品的库存
		this.reduceProductStock(orderItemList);
		//清空一下购物车
		this.cleanCart(cartList);

		//返回给前端数据

		OrderVo orderVo = assembleOrderVo(order, orderItemList);
		return ServerResponse.success(orderVo);
	}


	private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList) {
		OrderVo orderVo = new OrderVo();
		orderVo.setOrderNo(order.getOrderNo());
		orderVo.setPayment(order.getPayment());
		orderVo.setPaymentType(order.getPaymentType());
		orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());

		orderVo.setPostage(order.getPostage());
		orderVo.setStatus(order.getStatus());
		orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());

		orderVo.setShippingId(order.getShippingId());
		ShippingQuery query = new ShippingQuery();
		query.setId(order.getShippingId());
		Shipping shipping = shippingMapper.get(query);
		if (shipping != null) {
			orderVo.setReceiverName(shipping.getReceiverName());
			orderVo.setShippingVo(assembleShippingVo(shipping));
		}
		if(order.getPaymentTime()!=null) {
			orderVo.setPaymentTime(Const.DF.format(order.getPaymentTime()));
		}
		if(order.getSendTime()!=null) {
			orderVo.setSendTime(Const.DF.format(order.getSendTime()));
		}
		if(order.getEndTime()!=null) {
			orderVo.setEndTime(Const.DF.format(order.getEndTime()));
		}
		if(order.getCreateTime()!=null) {
			orderVo.setCreateTime(Const.DF.format(order.getCreateTime()));
		}
		if(order.getCloseTime()!=null) {
			orderVo.setCloseTime(Const.DF.format(order.getCloseTime()));
		}


		orderVo.setImageHost(Const.IMAGE_HOST);


		List<OrderItemVo> orderItemVoList = Lists.newArrayList();

		for (OrderItem orderItem : orderItemList) {
			OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
			orderItemVoList.add(orderItemVo);
		}
		orderVo.setOrderItemVoList(orderItemVoList);
		return orderVo;
	}


	private OrderItemVo assembleOrderItemVo(OrderItem orderItem) {
		OrderItemVo orderItemVo = new OrderItemVo();
		orderItemVo.setOrderNo(orderItem.getOrderNo());
		orderItemVo.setProductId(orderItem.getProductId());
		orderItemVo.setProductName(orderItem.getProductName());
		orderItemVo.setProductImage(orderItem.getProductImage());
		orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
		orderItemVo.setQuantity(orderItem.getQuantity());
		orderItemVo.setTotalPrice(orderItem.getTotalPrice());
		if (orderItem.getCreateTime() != null) {
			orderItemVo.setCreateTime(Const.DF.format(orderItem.getCreateTime()));
		}
		return orderItemVo;
	}


	private ShippingVo assembleShippingVo(Shipping shipping) {
		ShippingVo shippingVo = new ShippingVo();
		shippingVo.setReceiverName(shipping.getReceiverName());
		shippingVo.setReceiverAddress(shipping.getReceiverAddress());
		shippingVo.setReceiverProvince(shipping.getReceiverProvince());
		shippingVo.setReceiverCity(shipping.getReceiverCity());
		shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
		shippingVo.setReceiverMobile(shipping.getReceiverMobile());
		shippingVo.setReceiverZip(shipping.getReceiverZip());
		shippingVo.setReceiverPhone(shippingVo.getReceiverPhone());
		return shippingVo;
	}

	private void cleanCart(List<Cart> cartList) {
		cartMapper.deleteBatch(cartList);
	}


	private void reduceProductStock(List<OrderItem> orderItemList) {
		productMapper.reduceStock(orderItemList);
	}


	private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
		Order order = new Order();
		long orderNo = this.generateOrderNo();
		order.setOrderNo(orderNo);
		order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
		order.setPostage(0);
		order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
		order.setPayment(payment);

		order.setUserId(userId);
		order.setShippingId(shippingId);

		long rowCount = orderMapper.insert(order);
		if (rowCount > 0) {
			return order;
		}
		return null;
	}


	private long generateOrderNo() {
		long currentTime = System.currentTimeMillis();
		return currentTime + new Random().nextInt(100);
	}


	private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
		BigDecimal payment = new BigDecimal("0");
		for (OrderItem orderItem : orderItemList) {
			payment = BigDecimalUtils.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
		}
		return payment;
	}

	private ServerResponse getCartOrderItem(Integer userId, List<Cart> cartList) {
		List<OrderItem> orderItemList = Lists.newArrayList();
		if (CollectionUtils.isEmpty(cartList)) {
			return ServerResponse.error("购物车为空");
		}

		//校验购物车的数据,包括产品的状态和数量
		for (Cart cartItem : cartList) {
			OrderItem orderItem = new OrderItem();
			ProductQuery query = new ProductQuery();
			query.setId(cartItem.getProductId());
			Product product = productMapper.get(query);
			if (Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()) {
				return ServerResponse.error("产品" + product.getName() + "不是在线售卖状态");
			}

			//校验库存
			if (cartItem.getQuantity() > product.getStock()) {
				return ServerResponse.error("产品" + product.getName() + "库存不足");
			}

			orderItem.setUserId(userId);
			orderItem.setProductId(product.getId());
			orderItem.setProductName(product.getName());
			orderItem.setProductImage(product.getMainImage());
			orderItem.setCurrentUnitPrice(product.getPrice());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(), cartItem.getQuantity()));
			orderItemList.add(orderItem);
		}
		return ServerResponse.success(orderItemList);
	}


	@Override
	public ServerResponse cancel(Integer userId, Long orderNo) {
		OrderQuery query = new OrderQuery();
		query.setUserId(userId);
		query.setOrderNo(orderNo);
		Order order = orderMapper.get(query);
		if (order == null) {
			return ServerResponse.error("该用户此订单不存在");
		}
		if (order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()) {
			return ServerResponse.error("已付款,无法取消订单");
		}
		Order updateOrder = new Order();
		updateOrder.setId(order.getId());
		updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());

		int row = orderMapper.update(updateOrder);
		if (row > 0) {
			return ServerResponse.success();
		}
		return ServerResponse.error();
	}


	@Override
	public ServerResponse getOrderCartProduct(Integer userId) {
		OrderProductVo orderProductVo = new OrderProductVo();
		//从购物车中获取数据

		CartQuery query = new CartQuery();
		query.setUserId(userId);
		query.setChecked(1);
		List<Cart> cartList = cartMapper.find(query);
		ServerResponse serverResponse = this.getCartOrderItem(userId, cartList);
		if (!serverResponse.isSuccess()) {
			return serverResponse;
		}
		List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();

		List<OrderItemVo> orderItemVoList = Lists.newArrayList();

		BigDecimal payment = new BigDecimal("0");
		for (OrderItem orderItem : orderItemList) {
			payment = BigDecimalUtils.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
			orderItemVoList.add(assembleOrderItemVo(orderItem));
		}
		orderProductVo.setProductTotalPrice(payment);
		orderProductVo.setOrderItemVoList(orderItemVoList);
		orderProductVo.setImageHost(Const.IMAGE_HOST);
		return ServerResponse.success(orderProductVo);
	}


	@Override
	public ServerResponse getOrderDetail(Integer userId, Long orderNo) {
		OrderQuery query = new OrderQuery();
		query.setUserId(userId);
		query.setOrderNo(orderNo);
		Order order = orderMapper.get(query);
		if (order != null) {
			OrderItemQuery orderItemQuery = new OrderItemQuery();
			orderItemQuery.setUserId(userId);
			orderItemQuery.setOrderNo(orderNo);
			List<OrderItem> orderItemList = orderItemMapper.find(orderItemQuery);
			OrderVo orderVo = assembleOrderVo(order, orderItemList);
			return ServerResponse.success(orderVo);
		}
		return ServerResponse.error("没有找到该订单");
	}


	@Override
	public ServerResponse getOrderList(Integer userId, int pageNum, int pageSize) {
		OrderQuery query = new OrderQuery();
		query.setUserId(userId);
		query.setPageSize(pageSize);
		query.setCurrentPage(pageNum);
		List<Order> orderList = orderMapper.find(query);
		List<OrderVo> orderVoList = assembleOrderVoList(orderList, userId);
		PageInfo pageResult = new PageInfo();
		pageResult.init(orderMapper.count(query), pageNum, pageSize, orderVoList);
		return ServerResponse.success(pageResult);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public ServerResponse aliCallback(Map<String, String> params) throws ParseException {
		Long orderNo = Long.parseLong(params.get("out_trade_no"));
		String tradeNo = params.get("trade_no");
		String tradeStatus = params.get("trade_status");
		OrderQuery query = new OrderQuery();
		query.setOrderNo(orderNo);
		Order order = orderMapper.get(query);
		if (order == null) {
			return ServerResponse.error("非福利商城的订单,回调忽略");
		}
		if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
			return ServerResponse.success("支付宝重复调用");
		}
		if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
			order.setPaymentTime(Const.DF.parse(params.get("gmt_payment")));
			order.setStatus(Const.OrderStatusEnum.PAID.getCode());
			orderMapper.update(order);
		}

		PayInfo payInfo = new PayInfo();
		payInfo.setUserId(order.getUserId());
		payInfo.setOrderNo(order.getOrderNo());
		payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
		payInfo.setPlatformNumber(tradeNo);
		payInfo.setPlatformStatus(tradeStatus);

		payInfoMapper.insert(payInfo);

		return ServerResponse.success();
	}

	private List<OrderVo> assembleOrderVoList(List<Order> orderList, Integer userId) {
		List<OrderVo> orderVoList = Lists.newArrayList();
		for (Order order : orderList) {
			OrderItemQuery query = new OrderItemQuery();
			query.setOrderNo(order.getOrderNo());
			query.setUserId(userId);
			List<OrderItem> orderItemList = orderItemMapper.find(query);
			OrderVo orderVo = assembleOrderVo(order, orderItemList);
			orderVoList.add(orderVo);
		}
		return orderVoList;
	}

	@Override
	public ServerResponse pay(Long orderNo, Integer userId, String path) {
		Map<String, String> resultMap = Maps.newHashMap();
		OrderQuery query = new OrderQuery();
		query.setUserId(userId);
		query.setOrderNo(orderNo);
		Order order = orderMapper.get(query);
		if (order == null) {
			return ServerResponse.error("用户没有该订单");
		}
		resultMap.put("orderNo", String.valueOf(order.getOrderNo()));

		// (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
		// 需保证商户系统端不能重复，建议通过数据库sequence生成，
		String outTradeNo = order.getOrderNo().toString();

		// (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
		String subject = new StringBuilder().append("福利商城扫码支付,订单号:").append(outTradeNo).toString();

		// (必填) 订单总金额，单位为元，不能超过1亿元
		// 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
		String totalAmount = order.getPayment().toString();

		// (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
		// 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
		String undiscountableAmount = "0";

		// 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
		// 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
		String sellerId = "";

		// 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
		String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount).append("元").toString();

		// 商户操作员编号，添加此参数可以为商户操作员做销售统计
		String operatorId = "test_operator_id";

		// (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
		String storeId = "test_store_id";

		// 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
		ExtendParams extendParams = new ExtendParams();
		extendParams.setSysServiceProviderId("2088100200300400500");

		// 支付超时，定义为120分钟
		String timeoutExpress = "120m";

		// 商品明细列表，需填写购买商品详细信息，
		List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

		OrderItemQuery orderItemQuery = new OrderItemQuery();
		orderItemQuery.setUserId(userId);
		orderItemQuery.setOrderNo(orderNo);
		List<OrderItem> orderItemList = orderItemMapper.find(orderItemQuery);
		for (OrderItem orderItem : orderItemList) {
			GoodsDetail goods = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
				BigDecimalUtils.mul(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue()).longValue(),
				orderItem.getQuantity());
			goodsDetailList.add(goods);
		}

		// 创建扫码支付请求builder，设置请求参数
		AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
			.setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
			.setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
			.setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
			.setTimeoutExpress(timeoutExpress)
			.setNotifyUrl(Const.ALIPAY_CALLBACK_URL)//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
			.setGoodsDetailList(goodsDetailList);


		AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
		switch (result.getTradeStatus()) {
			case SUCCESS:
				log.info("支付宝预下单成功: )");

				AlipayTradePrecreateResponse response = result.getResponse();
				dumpResponse(response);

				File folder = new File(path);
				if (!folder.exists()) {
					folder.setWritable(true);
					folder.mkdirs();
				}

				// 需要修改为运行机器上的路径
				//细节细节细节
				String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
				String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
				ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

				File targetFile = new File(path, qrFileName);
				try {
					FTPUtils.uploadFile(Lists.newArrayList(targetFile));
				} catch (IOException e) {
					log.error("上传二维码异常", e);
				}
				log.info("qrPath:" + qrPath);
				String qrUrl = Const.IMAGE_HOST + targetFile.getName();
				resultMap.put("qrUrl", qrUrl);
				return ServerResponse.success(resultMap);
			case FAILED:
				log.error("支付宝预下单失败!!!");
				return ServerResponse.error("支付宝预下单失败!!!");

			case UNKNOWN:
				log.error("系统异常，预下单状态未知!!!");
				return ServerResponse.error("系统异常，预下单状态未知!!!");

			default:
				log.error("不支持的交易状态，交易返回异常!!!");
				return ServerResponse.error("不支持的交易状态，交易返回异常!!!");
		}

	}

	@Override
	public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo) {
		OrderQuery query = new OrderQuery();
		query.setUserId(userId);
		query.setOrderNo(orderNo);
		Order order = orderMapper.get(query);
		if (order == null) {
			return ServerResponse.error("用户没有该订单");
		}
		if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
			return ServerResponse.success();
		}
		return ServerResponse.error();
	}

	// 简单打印应答
	private void dumpResponse(AlipayResponse response) {
		if (response != null) {
			log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
			if (StringUtils.isNotEmpty(response.getSubCode())) {
				log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
					response.getSubMsg()));
			}
			log.info("body:" + response.getBody());
		}
	}

	@Override
	public ServerResponse manageList(int pageNum, int pageSize) {
		OrderQuery query = new OrderQuery();
		query.setPageSize(pageSize);
		query.setCurrentPage(pageNum);
		List<Order> orderList = orderMapper.find(query);
		List<OrderVo> orderVoList = this.assembleOrderVoList(orderList, null);
		PageInfo pageResult = new PageInfo();
		pageResult.init(orderMapper.count(query), pageNum, pageSize, orderVoList);
		return ServerResponse.success(pageResult);
	}


	@Override
	public ServerResponse manageDetail(Long orderNo) {
		OrderQuery query = new OrderQuery();
		query.setOrderNo(orderNo);
		Order order = orderMapper.get(query);
		if (order != null) {
			OrderItemQuery orderItemQuery = new OrderItemQuery();
			orderItemQuery.setOrderNo(orderNo);
			List<OrderItem> orderItemList = orderItemMapper.find(orderItemQuery);
			OrderVo orderVo = assembleOrderVo(order, orderItemList);
			return ServerResponse.success(orderVo);
		}
		return ServerResponse.error("订单不存在");
	}


	@Override
	public ServerResponse manageSearch(Long orderNo, int pageNum, int pageSize) {
		OrderQuery query = new OrderQuery();
		query.setOrderNo(orderNo);
		Order order = orderMapper.get(query);
		if (order != null) {
			OrderItemQuery orderItemQuery = new OrderItemQuery();
			orderItemQuery.setOrderNo(orderNo);
			orderItemQuery.setPageSize(pageSize);
			orderItemQuery.setCurrentPage(pageNum);
			List<OrderItem> orderItemList = orderItemMapper.find(orderItemQuery);
			OrderVo orderVo = assembleOrderVo(order, orderItemList);

			PageInfo pageResult = new PageInfo();
			pageResult.init(orderItemMapper.count(orderItemQuery), pageNum, pageSize, Lists.newArrayList(orderVo));
			return ServerResponse.success(pageResult);
		}
		return ServerResponse.error("订单不存在");
	}


	@Override
	public ServerResponse manageSendGoods(Long orderNo) {
		OrderQuery query = new OrderQuery();
		query.setOrderNo(orderNo);
		Order order = orderMapper.get(query);
		if (order != null) {
			if (order.getStatus() == Const.OrderStatusEnum.PAID.getCode()) {
				order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
				order.setSendTime(new Date());
				orderMapper.update(order);
				return ServerResponse.success((Object) "发货成功");
			}
		}
		return ServerResponse.error("订单不存在");
	}
}
