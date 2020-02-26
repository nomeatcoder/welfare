package cn.nomeatcoder.dal.mapper;

import cn.nomeatcoder.common.domain.Order;
import cn.nomeatcoder.common.query.OrderQuery;

/**
 * Mapper
 *
 * @author Chenzhe Mao
 * @ClassName: OrderMapper
 * @Date 2020-01-31 14:38:45
 */
public interface OrderMapper extends Mapper<OrderQuery, Order> {
	int delete(Order order);
}