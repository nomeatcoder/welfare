package cn.nomeatcoder.dal.mapper;

import cn.nomeatcoder.common.Mapper;
import cn.nomeatcoder.common.domain.Product;
import cn.nomeatcoder.common.query.ProductQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper
 *
 * @author Chenzhe Mao
 * @ClassName: ProductMapper
 * @Date 2020-01-31 14:38:45
 */
public interface ProductMapper extends Mapper<ProductQuery, Product> {
	List<Product> selectByNameAndProductId(ProductQuery query);
	List<Product> selectByNameAndCategoryIds(ProductQuery query);

}