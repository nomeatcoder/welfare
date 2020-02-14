package cn.nomeatcoder.service.impl;

import afu.org.checkerframework.checker.units.qual.C;
import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.domain.Category;
import cn.nomeatcoder.common.query.CategoryQuery;
import cn.nomeatcoder.dal.mapper.CategoryMapper;
import cn.nomeatcoder.service.CategoryService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Slf4j
@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

	@Resource
	private CategoryMapper categoryMapper;

	@Override
	public ServerResponse addCategory(String categoryName, Integer parentId) {
		if (parentId == null || StringUtils.isBlank(categoryName)) {
			return ServerResponse.error("添加品类参数错误");
		}

		Category category = new Category();
		category.setName(categoryName);
		category.setParentId(parentId);
		category.setStatus(true);

		long rowCount = categoryMapper.insert(category);
		if (rowCount > 0) {
			return ServerResponse.success("添加品类成功");
		}
		return ServerResponse.error("添加品类失败");
	}

    @Override
    public ServerResponse delCategory(Integer categoryId) {
        if (categoryId == null ) {
            return ServerResponse.error("删除品类参数错误");
        }

        long rowCount = categoryMapper.delete(categoryId);
        if (rowCount > 0) {
            return ServerResponse.success("删除品类成功");
        }
        return ServerResponse.error("删除品类失败");
    }

    @Override
	public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
		if (categoryId == null || StringUtils.isBlank(categoryName)) {
			return ServerResponse.error("更新品类参数错误");
		}
		Category category = new Category();
		category.setId(categoryId);
		category.setName(categoryName);

		int rowCount = categoryMapper.update(category);
		if (rowCount > 0) {
			return ServerResponse.success("更新品类名字成功");
		}
		return ServerResponse.error("更新品类名字失败");
	}

	@Override
	public ServerResponse getChildrenParallelCategory(Integer categoryId) {
        CategoryQuery query = new CategoryQuery();
        query.setParentId(categoryId);
		List<Category> categoryList = categoryMapper.find(query);
		if (CollectionUtils.isEmpty(categoryList)) {
			log.info("[getChildrenParallelCategory] no mapping record, categoryId:{}",categoryId);
		}
		return ServerResponse.success(categoryList);
	}


	/**
	 * 递归查询本节点的id及孩子节点的id
	 *
	 * @param categoryId
	 * @return
	 */
	@Override
	public ServerResponse selectCategoryAndChildrenById(Integer categoryId) {
		Set<Category> categorySet = Sets.newHashSet();
		findChildCategory(categorySet, categoryId);

		List<Integer> categoryIdList = Lists.newArrayList();
		if (categoryId != null) {
			for (Category categoryItem : categorySet) {
				categoryIdList.add(categoryItem.getId());
			}
		}
		return ServerResponse.success(categoryIdList);
	}

	private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId) {
	    CategoryQuery query = new CategoryQuery();
	    query.setId(categoryId);
		Category category = categoryMapper.get(query);
		if (category != null) {
			categorySet.add(category);
		}

        query = new CategoryQuery();
        query.setParentId(categoryId);
		List<Category> categoryList = categoryMapper.find(query);
		for (Category categoryItem : categoryList) {
			findChildCategory(categorySet, categoryItem.getId());
		}
		return categorySet;
	}

}
