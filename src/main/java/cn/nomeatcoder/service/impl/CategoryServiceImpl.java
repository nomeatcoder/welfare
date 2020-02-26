package cn.nomeatcoder.service.impl;

import cn.nomeatcoder.common.Const;
import cn.nomeatcoder.common.MyCache;
import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.domain.Category;
import cn.nomeatcoder.common.query.CategoryQuery;
import cn.nomeatcoder.common.vo.CategoryDetailVo;
import cn.nomeatcoder.common.vo.CategoryVo;
import cn.nomeatcoder.common.vo.IndexVo;
import cn.nomeatcoder.dal.mapper.CategoryMapper;
import cn.nomeatcoder.service.CategoryService;
import cn.nomeatcoder.utils.GsonUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

	@Resource
	private CategoryMapper categoryMapper;

	@Resource
	private MyCache myCache;

	@Override
	public ServerResponse getIndex() {
		String json = myCache.getKey(MyCache.INDEX_INFO_KEY);
		//缓存中不存在
		if (json == null) {
			//查询数据库
			IndexVo indexVo = getIndexVo();
			//放入缓存中
			myCache.setKey(MyCache.INDEX_INFO_KEY, GsonUtils.toJson(indexVo));
			return ServerResponse.success(indexVo);
		}
		//缓存中存在
		IndexVo indexVo = GsonUtils.fromGson2Obj(json, IndexVo.class);
		return ServerResponse.success(indexVo);
	}

	@Override
	public IndexVo getIndexVo() {
		CategoryQuery query = new CategoryQuery();
		List<Category> list = categoryMapper.find(query);
		HashMap<Integer, List<Category>> map = new HashMap<>();
		HashMap<Integer, String> mappingMap = new HashMap<>();
		for (Category category : list) {
			//如果是一级分类
			if (category.getParentId() == 0) {
				mappingMap.put(category.getId(), category.getName());
			}
			//如果是二级分类
			else {
				List<Category> cartgoryList = map.getOrDefault(category.getParentId(), new ArrayList<>());
				cartgoryList.add(category);
				map.put(category.getParentId(), cartgoryList);
			}
		}
		IndexVo indexVo = new IndexVo();
		indexVo.setList(new ArrayList<>());
		for (List<Category> value : map.values()) {
			if (value != null && value.size() > 0) {
				CategoryDetailVo categoryDetailVo = new CategoryDetailVo();
				categoryDetailVo.setName(mappingMap.get(value.get(0).getParentId()));
				categoryDetailVo.setParentId(value.get(0).getParentId());
				categoryDetailVo.setSubList(value.stream().map(v -> {
					CategoryVo categoryVo = new CategoryVo();
					categoryVo.setId(v.getId());
					categoryVo.setName(v.getName());
					categoryVo.setParentId(v.getParentId());
					categoryVo.setImage(v.getImage());
					return categoryVo;
				}).collect(Collectors.toList()));
				indexVo.getList().add(categoryDetailVo);
			}
		}
		indexVo.setImageHost(Const.IMAGE_HOST);
		return indexVo;
	}

	@Override
	public ServerResponse saveOrUpdateCategory(Integer categoryId, String categoryName, Integer parentId, String image) {
		if (categoryId == null || parentId == null || StringUtils.isBlank(categoryName)) {
			return ServerResponse.error("添加品类参数错误");
		}
		Category category = new Category();

		category.setName(categoryName);
		category.setParentId(parentId);
		category.setStatus(true);

		if(parentId!=0 && StringUtils.isNotBlank(image)){
			category.setImage(image);
		}
		if (categoryId == 0) {
			long rowCount = categoryMapper.insert(category);
			if (rowCount > 0) {
				return ServerResponse.success("添加品类成功");
			}
			return ServerResponse.error("添加品类失败");
		} else {
			category.setId(categoryId);
			long rowCount = categoryMapper.update(category);
			if (rowCount > 0) {
				return ServerResponse.success("更新品类成功");
			}
			return ServerResponse.error("更新品类失败");
		}
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

	@Override
	public ServerResponse getCategoryDetail(Integer categoryId) {
		CategoryQuery query = new CategoryQuery();
		query.setId(categoryId);
		Category category = categoryMapper.get(query);
		if (category == null) {
			return ServerResponse.error("查询的品类不存在");
		}
		return ServerResponse.success(category);
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
