package cn.nomeatcoder.service.impl;


import cn.nomeatcoder.common.*;
import cn.nomeatcoder.common.domain.Category;
import cn.nomeatcoder.common.domain.Product;
import cn.nomeatcoder.common.query.CategoryQuery;
import cn.nomeatcoder.common.query.ProductQuery;
import cn.nomeatcoder.common.vo.ProductDetailVo;
import cn.nomeatcoder.common.vo.ProductListVo;
import cn.nomeatcoder.dal.mapper.CategoryMapper;
import cn.nomeatcoder.dal.mapper.ProductMapper;
import cn.nomeatcoder.service.CategoryService;
import cn.nomeatcoder.service.ProductService;
import cn.nomeatcoder.utils.GsonUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service("productService")
public class ProductServiceImpl implements ProductService {


	@Resource
	private ProductMapper productMapper;

	@Resource
	private CategoryMapper categoryMapper;

	@Resource
	private CategoryService categoryService;

	@Resource
	private MyCache myCache;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public ServerResponse saveOrUpdateProduct(Product product) {
		if (product != null) {
			if (StringUtils.isNotBlank(product.getSubImages())) {
				String[] subImageArray = product.getSubImages().split(",");
				if (subImageArray.length > 0) {
					product.setMainImage(subImageArray[0]);
				}
			}

			if (product.getId() != null) {
				int rowCount = productMapper.update(product);
				if (rowCount > 0) {
					updateProductDetailVoCache(product.getId(), product);
					return ServerResponse.success("更新产品成功");
				}
				return ServerResponse.error("更新产品失败");
			} else {
				int rowCount = (int) productMapper.insert(product);
				if (rowCount > 0) {
					updateProductDetailVoCache(product.getId(), product);
					return ServerResponse.success("新增产品成功");
				}
				return ServerResponse.error("新增产品失败");
			}
		}
		return ServerResponse.error("新增或更新产品参数不正确");
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public ServerResponse setSaleStatus(Integer productId, Integer status) {
		if (productId == null || status == null) {
			return ServerResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		ProductQuery query = new ProductQuery();
		query.setId(productId);
		Product product = productMapper.get(query);
		if(product == null){
			return ServerResponse.error("产品不存在");
		}
		product.setStatus(status);
		int rowCount = productMapper.update(product);
		if (rowCount > 0) {
			updateProductDetailVoCache(product.getId(), product);
			return ServerResponse.success("修改产品销售状态成功");
		}
		return ServerResponse.error("修改产品销售状态失败");
	}


	@Override
	public ServerResponse manageProductDetail(Integer productId) {
		if (productId == null) {
			return ServerResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		ProductQuery query = new ProductQuery();
		query.setId(productId);
		Product product = productMapper.get(query);
		if (product == null) {
			return ServerResponse.error("产品已下架或者删除");
		}
		ProductDetailVo productDetailVo = assembleProductDetailVo(product);
		return ServerResponse.success(productDetailVo);
	}

	private ProductDetailVo assembleProductDetailVo(Product product) {
		ProductDetailVo productDetailVo = new ProductDetailVo();
		productDetailVo.setId(product.getId());
		productDetailVo.setSubtitle(product.getSubtitle());
		productDetailVo.setPrice(product.getPrice());
		productDetailVo.setMainImage(product.getMainImage());
		productDetailVo.setSubImages(product.getSubImages());
		productDetailVo.setCategoryId(product.getCategoryId());
		productDetailVo.setDetail(product.getDetail());
		productDetailVo.setName(product.getName());
		productDetailVo.setStatus(product.getStatus());
		productDetailVo.setStock(product.getStock());

		productDetailVo.setImageHost(Const.IMAGE_HOST);

		CategoryQuery query = new CategoryQuery();
		query.setId(product.getCategoryId());
		Category category = categoryMapper.get(query);
		if (category == null) {
			//默认根节点
			productDetailVo.setParentCategoryId(0);
		} else {
			productDetailVo.setParentCategoryId(category.getParentId());
		}

		if(product.getCreateTime()!=null) {
			productDetailVo.setCreateTime(Const.DF.format(product.getCreateTime()));
		}
		if(product.getUpdateTime()!=null) {
			productDetailVo.setUpdateTime(Const.DF.format(product.getUpdateTime()));
		}
		return productDetailVo;
	}


	@Override
	public ServerResponse getProductList(int pageNum, int pageSize) {

		ProductQuery query = new ProductQuery();
		query.setPageSize(pageSize);
		query.setCurrentPage(pageNum);
		List<Product> productList = productMapper.find(query);

		List<ProductListVo> productListVoList = Lists.newArrayList();
		for (Product productItem : productList) {
			ProductListVo productListVo = assembleProductListVo(productItem);
			productListVoList.add(productListVo);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.init(productMapper.count(query), pageNum, pageSize, productListVoList);
		return ServerResponse.success(pageInfo);
	}

	private ProductListVo assembleProductListVo(Product product) {
		ProductListVo productListVo = new ProductListVo();
		productListVo.setId(product.getId());
		productListVo.setName(product.getName());
		productListVo.setCategoryId(product.getCategoryId());
		productListVo.setImageHost(Const.IMAGE_HOST);
		productListVo.setMainImage(product.getMainImage());
		productListVo.setPrice(product.getPrice());
		productListVo.setSubtitle(product.getSubtitle());
		productListVo.setStatus(product.getStatus());
		return productListVo;
	}


	@Override
	public ServerResponse searchProduct(String productName, Integer productId, int pageNum, int pageSize) {

		if (StringUtils.isNotBlank(productName)) {
			productName = new StringBuilder().append("%").append(productName).append("%").toString();
		}
		ProductQuery query = new ProductQuery();
		query.setPageSize(pageSize);
		query.setCurrentPage(pageNum);
		query.setName(productName);
		query.setId(productId);
		List<Product> productList = productMapper.selectByNameAndProductId(query);
		List<ProductListVo> productListVoList = Lists.newArrayList();
		for (Product productItem : productList) {
			ProductListVo productListVo = assembleProductListVo(productItem);
			productListVoList.add(productListVo);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.init(productMapper.count(query), pageNum, pageSize, productListVoList);
		return ServerResponse.success(pageInfo);
	}


	@Override
	public ServerResponse getProductDetail(Integer productId) {
		if (productId == null) {
			return ServerResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		String key = String.format(MyCache.PRODUCT_DETAIL_KEY, productId);
		String json = myCache.getKey(key);
		Product product;
		if (json == null) {
			product = getProduct(productId);
			if (product != null) {
				myCache.setKey(key, GsonUtils.toJson(product));
			}
		} else {
			product = GsonUtils.fromGson2Obj(json, Product.class);
		}
		if (product == null) {
			return ServerResponse.error("产品不存在");
		}
		if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
			return ServerResponse.error("产品已下架或者删除");
		}
		return ServerResponse.success(assembleProductDetailVo(product));
	}

	@Override
	public Product getProduct(Integer productId) {
		ProductQuery query = new ProductQuery();
		query.setId(productId);
		Product product = productMapper.get(query);
		return product;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public ServerResponse deleteProduct(Integer productId) {
		if (productId == null) {
			return ServerResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		ProductQuery query = new ProductQuery();
		query.setId(productId);
		int rowCount = productMapper.delete(query);
		if (rowCount > 0) {
			updateProductDetailVoCache(productId, null);
			return ServerResponse.success("删除产品成功");
		}
		return ServerResponse.error("删除产品失败");
	}


	@Override
	public ServerResponse getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy) {
		if (StringUtils.isBlank(keyword) && categoryId == null) {
			return ServerResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		List<Integer> categoryIdList = new ArrayList<Integer>();

		if (categoryId != null) {
			CategoryQuery query = new CategoryQuery();
			query.setId(categoryId);
			Category category = categoryMapper.get(query);
			if (category == null && StringUtils.isBlank(keyword)) {
				PageInfo pageInfo = new PageInfo();
				return ServerResponse.success(pageInfo);
			}
			categoryIdList = (List<Integer>) categoryService.selectCategoryAndChildrenById(category.getId()).getData();
		}
		if (StringUtils.isNotBlank(keyword)) {
			keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
		}

		ProductQuery query = new ProductQuery();
		query.setPageSize(pageSize);
		query.setCurrentPage(pageNum);
		query.setCategoryIdList(categoryIdList);
		query.setName(keyword);
		//排序处理
		if (StringUtils.isNotBlank(orderBy)) {
			if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
				String[] orderByArray = orderBy.split("_");
				query.putOrderBy(orderByArray[0], Const.ASC.equals(orderByArray[1]));
				query.setOrderByEnable(true);
			}
		}
		List<Product> productList = productMapper.selectByNameAndCategoryIds(query);

		List<ProductListVo> productListVoList = Lists.newArrayList();
		for (Product product : productList) {
			ProductListVo productListVo = assembleProductListVo(product);
			productListVoList.add(productListVo);
		}

		PageInfo pageInfo = new PageInfo();
		pageInfo.init(productMapper.count(query), pageNum, pageSize, productListVoList);
		return ServerResponse.success(pageInfo);
	}


	private void updateProductDetailVoCache(Integer productId, Product product) {
		ProductDetailVo productDetailVo = product == null ? new ProductDetailVo() : assembleProductDetailVo(product);
		myCache.setKey(String.format(MyCache.PRODUCT_DETAIL_KEY, productId), GsonUtils.toJson(productDetailVo));
	}

}
