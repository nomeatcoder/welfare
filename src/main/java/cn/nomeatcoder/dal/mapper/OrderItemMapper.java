package cn.nomeatcoder.dal.mapper;

import cn.nomeatcoder.common.domain.OrderItem;
import cn.nomeatcoder.common.query.OrderItemQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper
 *
 * @author Chenzhe Mao
 * @ClassName: OrderItemMapper
 * @Date 2020-01-31 14:38:45
 */
public interface OrderItemMapper extends Mapper<OrderItemQuery, OrderItem> {
	void batchInsert(@Param("orderItemList") List<OrderItem> orderItemList);
}