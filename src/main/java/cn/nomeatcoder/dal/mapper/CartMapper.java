package cn.nomeatcoder.dal.mapper;

import cn.nomeatcoder.common.Mapper;
import cn.nomeatcoder.common.domain.Cart;
import cn.nomeatcoder.common.query.CartQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper
 * @ClassName: CartMapper
 * @author Chenzhe Mao
 * @Date 2020-01-31 14:38:45
 */
public interface CartMapper extends Mapper<CartQuery, Cart> {
	int deleteByUserIdProductIds(CartQuery query);
	int checkedOrUncheckedProduct(@Param("userId") Integer userId,@Param("productId")Integer productId,@Param("checked") Integer checked);
	int selectCartProductCheckedStatusByUserId(Integer userId);
	int selectCartProductCount(@Param("userId") Integer userId);

	void deleteBatch(List<Cart> list);
}