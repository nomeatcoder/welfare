package cn.nomeatcoder.controller.backend;

import cn.nomeatcoder.common.Const;
import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.domain.Product;
import cn.nomeatcoder.common.domain.User;
import cn.nomeatcoder.service.FileService;
import cn.nomeatcoder.service.ProductService;
import cn.nomeatcoder.service.UserService;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("manage/product")
public class ProductManageController {

	@Resource
	private ProductService productService;
	@Resource
	private FileService fileService;

	@RequestMapping("save.do")
	@ResponseBody
	public ServerResponse productSave(HttpSession session, Product product) {
		return productService.saveOrUpdateProduct(product);
	}

	@RequestMapping("set_sale_status.do")
	@ResponseBody
	public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
		return productService.setSaleStatus(productId, status);
	}

	@RequestMapping("detail.do")
	@ResponseBody
	public ServerResponse getDetail(HttpSession session, Integer productId) {
		return productService.manageProductDetail(productId);
	}

	@RequestMapping("del_product.do")
	@ResponseBody
	public ServerResponse deleteProduct(HttpSession session, Integer productId) {
		return productService.deleteProduct(productId);
	}

	@RequestMapping("list.do")
	@ResponseBody
	public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		return productService.getProductList(pageNum, pageSize);
	}

	@RequestMapping("search.do")
	@ResponseBody
	public ServerResponse productSearch(HttpSession session, String productName, Integer productId, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		return productService.searchProduct(productName, productId, pageNum, pageSize);
	}

	@RequestMapping("upload.do")
	@ResponseBody
	public ServerResponse upload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {
		String path = request.getSession().getServletContext().getRealPath("upload");
		String targetFileName = fileService.upload(file, path);
		String url = Const.IMAGE_HOST + targetFileName;

		Map fileMap = Maps.newHashMap();
		fileMap.put("uri", targetFileName);
		fileMap.put("url", url);
		return ServerResponse.success(fileMap);
	}


	@RequestMapping("richtext_img_upload.do")
	@ResponseBody
	public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
		Map resultMap = Maps.newHashMap();

		String path = request.getSession().getServletContext().getRealPath("upload");
		String targetFileName = fileService.upload(file, path);
		if (StringUtils.isBlank(targetFileName)) {
			resultMap.put("success", false);
			resultMap.put("msg", "上传失败");
			return resultMap;
		}
		String url = Const.IMAGE_HOST + targetFileName;
		resultMap.put("success", true);
		resultMap.put("msg", "上传成功");
		resultMap.put("file_path", url);
		response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
		return resultMap;
	}


}
