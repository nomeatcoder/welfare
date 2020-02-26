package cn.nomeatcoder.service;


import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.vo.IndexVo;


public interface CategoryService {
    ServerResponse getIndex();

	IndexVo getIndexVo();

	ServerResponse saveOrUpdateCategory(Integer categoryId, String categoryName, Integer parentId, String image);
    ServerResponse delCategory(Integer parentId);
    ServerResponse updateCategoryName(Integer categoryId, String categoryName);
    ServerResponse getChildrenParallelCategory(Integer categoryId);
    ServerResponse selectCategoryAndChildrenById(Integer categoryId);
    ServerResponse getCategoryDetail(Integer categoryId);
}
