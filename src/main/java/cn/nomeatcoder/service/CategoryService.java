package cn.nomeatcoder.service;


import cn.nomeatcoder.common.ServerResponse;

/**
 * Created by geely
 */
public interface CategoryService {
    ServerResponse addCategory(String categoryName, Integer parentId, String image);
    ServerResponse delCategory(Integer parentId);
    ServerResponse updateCategoryName(Integer categoryId, String categoryName);
    ServerResponse getChildrenParallelCategory(Integer categoryId);
    ServerResponse selectCategoryAndChildrenById(Integer categoryId);

}
