package cn.nomeatcoder.service;

import cn.nomeatcoder.common.ServerResponse;

import java.text.ParseException;
import java.util.Map;

public interface OrderService {
	ServerResponse pay(Long orderNo, Integer userId, String path);

	ServerResponse aliCallback(Map<String, String> params) throws ParseException;

	ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

	ServerResponse createOrder(Integer userId, Integer shippingId);

	ServerResponse cancel(Integer userId, Long orderNo);

	ServerResponse del(Integer userId, Long orderNo);

	ServerResponse getOrderCartProduct(Integer userId);

	ServerResponse getOrderDetail(Integer userId, Long orderNo);

	ServerResponse getOrderList(Integer userId, int pageNum, int pageSize);

	ServerResponse manageList(int pageNum, int pageSize);

	ServerResponse manageDetail(Long orderNo);

	ServerResponse manageSearch(Long orderNo, int pageNum, int pageSize);

	ServerResponse manageSendGoods(Long orderNo);

}
