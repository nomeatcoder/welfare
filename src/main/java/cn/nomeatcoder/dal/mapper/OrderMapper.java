package cn.nomeatcoder.dal.mapper;

import cn.nomeatcoder.common.domain.Order;
import cn.nomeatcoder.common.query.OrderQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper
 *
 * @author Chenzhe Mao
 * @ClassName: OrderMapper
 * @Date 2020-01-31 14:38:45
 */
public interface OrderMapper extends Mapper<OrderQuery, Order> {
	int delete(Order order);

	List<Order> selectOrderStatusByCreateTime(@Param("status") Integer status, @Param("date") String date);
}