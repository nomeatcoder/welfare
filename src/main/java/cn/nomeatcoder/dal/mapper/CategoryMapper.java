package cn.nomeatcoder.dal.mapper;

import cn.nomeatcoder.common.domain.Category;
import cn.nomeatcoder.common.query.CategoryQuery;

/**
 * Mapper
 * @ClassName: CategoryMapper
 * @author Chenzhe Mao
 * @Date 2020-01-31 14:38:45
 */
public interface CategoryMapper extends Mapper<CategoryQuery, Category> {
	int delete(Integer id);
}