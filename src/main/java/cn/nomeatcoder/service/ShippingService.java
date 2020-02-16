package cn.nomeatcoder.service;


import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.domain.Shipping;

public interface ShippingService {

    ServerResponse add(Integer userId, Shipping shipping);
    ServerResponse del(Integer userId, Integer shippingId);
    ServerResponse update(Integer userId, Shipping shipping);
    ServerResponse select(Integer userId, Integer shippingId);
    ServerResponse list(Integer userId, int pageNum, int pageSize);

}
