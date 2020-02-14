package cn.nomeatcoder.service;


import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.domain.Product;

public interface ProductService {

    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse setSaleStatus(Integer productId, Integer status);

    ServerResponse manageProductDetail(Integer productId);

    ServerResponse getProductList(int pageNum, int pageSize);

    ServerResponse searchProduct(String productName, Integer productId, int pageNum, int pageSize);

    ServerResponse getProductDetail(Integer productId);

    ServerResponse getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy);



}
