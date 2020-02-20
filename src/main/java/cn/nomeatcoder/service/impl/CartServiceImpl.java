package cn.nomeatcoder.service.impl;


import cn.nomeatcoder.common.Const;
import cn.nomeatcoder.common.ResponseCode;
import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.domain.Cart;
import cn.nomeatcoder.common.domain.Product;
import cn.nomeatcoder.common.query.CartQuery;
import cn.nomeatcoder.common.query.ProductQuery;
import cn.nomeatcoder.common.vo.CartProductVo;
import cn.nomeatcoder.common.vo.CartVo;
import cn.nomeatcoder.dal.mapper.CartMapper;
import cn.nomeatcoder.dal.mapper.ProductMapper;
import cn.nomeatcoder.service.CartService;
import cn.nomeatcoder.utils.BigDecimalUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service("cartService")
public class CartServiceImpl implements CartService {

	@Resource
	private CartMapper cartMapper;
	@Resource
	private ProductMapper productMapper;

	@Override
	public ServerResponse add(Integer userId, Integer productId, Integer count) {
		if (productId == null || count == null) {
			return ServerResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}

		CartQuery query = new CartQuery();
		query.setUserId(userId);
		query.setProductId(productId);
		Cart cart = cartMapper.get(query);
		if (cart == null) {
			Cart cartItem = new Cart();
			cartItem.setQuantity(count);
			cartItem.setChecked(Const.Cart.CHECKED);
			cartItem.setProductId(productId);
			cartItem.setUserId(userId);
			cartMapper.insert(cartItem);
		} else {
			//这个产品已经在购物车里了.
			//如果产品已存在,数量相加
			count = cart.getQuantity() + count;
			cart.setQuantity(count);
			cartMapper.update(cart);
		}
		return this.list(userId);
	}

	@Override
	public ServerResponse update(Integer userId, Integer productId, Integer count) {
		if (productId == null || count == null) {
			return ServerResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		CartQuery query = new CartQuery();
		query.setUserId(userId);
		query.setProductId(productId);
		Cart cart = cartMapper.get(query);
		if (cart != null) {
			cart.setQuantity(count);
		}
		cartMapper.update(cart);
		return this.list(userId);
	}

	@Override
	public ServerResponse deleteProduct(Integer userId, String productIds) {
		List<Integer> productIdList = Splitter.on(",").splitToList(productIds).stream().map(v -> Integer.parseInt(v)).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(productIdList)) {
			return ServerResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		CartQuery query = new CartQuery();
		query.setUserId(userId);
		query.setProductIdList(productIdList);
		cartMapper.deleteByUserIdProductIds(query);
		return this.list(userId);
	}


	@Override
	public ServerResponse list(Integer userId) {
		CartVo cartVo = this.getCartVoLimit(userId);
		return ServerResponse.success(cartVo);
	}


	@Override
	public ServerResponse selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
		cartMapper.checkedOrUncheckedProduct(userId, productId, checked);
		return this.list(userId);
	}

	@Override
	public ServerResponse getCartProductCount(Integer userId) {
		if (userId == null) {
			return ServerResponse.success(0);
		}
		return ServerResponse.success(cartMapper.selectCartProductCount(userId));
	}

	private CartVo getCartVoLimit(Integer userId) {
		CartVo cartVo = new CartVo();
		CartQuery query = new CartQuery();
		query.setUserId(userId);
		List<Cart> cartList = cartMapper.find(query);
		List<CartProductVo> cartProductVoList = Lists.newArrayList();

		BigDecimal cartTotalPrice = new BigDecimal("0");

		if (cartList != null && cartList.size() > 0) {
			for (Cart cartItem : cartList) {
				CartProductVo cartProductVo = new CartProductVo();
				cartProductVo.setId(cartItem.getId());
				cartProductVo.setUserId(userId);
				cartProductVo.setProductId(cartItem.getProductId());

				ProductQuery productQuery = new ProductQuery();
				productQuery.setId(cartItem.getProductId());

				Product product = productMapper.get(productQuery);
				if (product != null) {
					cartProductVo.setProductMainImage(product.getMainImage());
					cartProductVo.setProductName(product.getName());
					cartProductVo.setProductSubtitle(product.getSubtitle());
					cartProductVo.setProductStatus(product.getStatus());
					cartProductVo.setProductPrice(product.getPrice());
					cartProductVo.setProductStock(product.getStock());
					//判断库存
					int buyLimitCount = 0;
					if (product.getStock() >= cartItem.getQuantity()) {
						//库存充足的时候
						buyLimitCount = cartItem.getQuantity();
						cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
					} else {
						buyLimitCount = product.getStock();
						cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
						//购物车中更新有效库存
						Cart cartForQuantity = new Cart();
						cartForQuantity.setId(cartItem.getId());
						cartForQuantity.setQuantity(buyLimitCount);
						cartMapper.update(cartForQuantity);
					}
					cartProductVo.setQuantity(buyLimitCount);
					//计算总价
					cartProductVo.setProductTotalPrice(BigDecimalUtils.mul(product.getPrice(), new BigDecimal(cartProductVo.getQuantity())));
					cartProductVo.setProductChecked(cartItem.getChecked());
				}

				if (cartItem.getChecked() == Const.Cart.CHECKED) {
					//如果已经勾选,增加到整个的购物车总价中
					cartTotalPrice = BigDecimalUtils.add(cartTotalPrice, cartProductVo.getProductTotalPrice());
				}
				cartProductVoList.add(cartProductVo);
			}
		}
		cartVo.setCartTotalPrice(cartTotalPrice);
		cartVo.setCartProductVoList(cartProductVoList);
		cartVo.setAllChecked(this.getAllCheckedStatus(userId));
		cartVo.setImageHost(Const.IMAGE_HOST);

		return cartVo;
	}

	private boolean getAllCheckedStatus(Integer userId) {
		if (userId == null) {
			return false;
		}
		return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;

	}


}
