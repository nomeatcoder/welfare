package cn.nomeatcoder.controller.protal;

import cn.nomeatcoder.common.Const;
import cn.nomeatcoder.common.ResponseCode;
import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.domain.User;
import cn.nomeatcoder.service.OrderService;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;


@Slf4j
@Controller
@RequestMapping("order")
public class OrderController {

    @Resource
    private OrderService orderService;

	@RequestMapping("create.do")
	@ResponseBody
	public ServerResponse create(HttpSession session, Integer shippingId){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user ==null){
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return orderService.createOrder(user.getId(),shippingId);
	}


	@RequestMapping("cancel.do")
	@ResponseBody
	public ServerResponse cancel(HttpSession session, Long orderNo){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user ==null){
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return orderService.cancel(user.getId(),orderNo);
	}

	@RequestMapping("del.do")
	@ResponseBody
	public ServerResponse del(HttpSession session, Long orderNo){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user ==null){
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return orderService.del(user.getId(),orderNo);
	}


	@RequestMapping("get_order_cart_product.do")
	@ResponseBody
	public ServerResponse getOrderCartProduct(HttpSession session){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user ==null){
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return orderService.getOrderCartProduct(user.getId());
	}



	@RequestMapping("detail.do")
	@ResponseBody
	public ServerResponse detail(HttpSession session,Long orderNo){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user ==null){
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return orderService.getOrderDetail(user.getId(),orderNo);
	}

	@RequestMapping("list.do")
	@ResponseBody
	public ServerResponse list(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user ==null){
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return orderService.getOrderList(user.getId(),pageNum,pageSize);
	}

	@RequestMapping("pay.do")
	@ResponseBody
	public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		String path = request.getSession().getServletContext().getRealPath("upload");
		return orderService.pay(orderNo, user.getId(), path);
	}

	@RequestMapping("alipay_callback.do")
	@ResponseBody
	public Object alipayCallback(HttpServletRequest request) {
		Map<String, String> params = Maps.newHashMap();

		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {

				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		log.info("支付宝回调,sign:{},trade_status:{},参数:{}", params.get("sign"), params.get("trade_status"), params.toString());

		//非常重要,验证回调的正确性,是不是支付宝发的.并且呢还要避免重复通知.

		params.remove("sign_type");
		try {
			boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());

			if (!alipayRSACheckedV2) {
				return ServerResponse.error("非法请求,验证不通过");
			}
		} catch (AlipayApiException e) {
			log.error("支付宝验证回调异常", e);
		}

		//todo 验证各种数据


		//
		ServerResponse serverResponse = null;
		try {
			serverResponse = orderService.aliCallback(params);
		} catch (ParseException e) {
			log.error("回调失败",e);
		}
		if (serverResponse.isSuccess()) {
			return Const.AlipayCallback.RESPONSE_SUCCESS;
		}
		return Const.AlipayCallback.RESPONSE_FAILED;
	}

	@RequestMapping("query_order_pay_status.do")
	@ResponseBody
	public ServerResponse queryOrderPayStatus(HttpSession session, Long orderNo){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user ==null){
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}

		return orderService.queryOrderPayStatus(user.getId(), orderNo);
	}

}
