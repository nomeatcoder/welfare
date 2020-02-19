package cn.nomeatcoder.controller.protal;

import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("product")
public class ProductController {

	@Resource
	private ProductService productService;


	@RequestMapping("detail.do")
	@ResponseBody
	public ServerResponse detail(Integer productId) {
		return productService.getProductDetail(productId);
	}

	@RequestMapping("list.do")
	@ResponseBody
	public ServerResponse list(@RequestParam(value = "keyword", required = false) String keyword,
	                           @RequestParam(value = "categoryId", required = false) Integer categoryId,
	                           @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
	                           @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
	                           @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {
		return productService.getProductByKeywordCategory(keyword, categoryId, pageNum, pageSize, orderBy);
	}


}
