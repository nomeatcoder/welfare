package cn.nomeatcoder.controller.backend;

import cn.nomeatcoder.common.Const;
import cn.nomeatcoder.common.ResponseCode;
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
	private UserService userService;
	@Resource
	private ProductService productService;
	@Resource
	private FileService fileService;

	@RequestMapping("save.do")
	@ResponseBody
	public ServerResponse productSave(HttpSession session, Product product) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");

		}
		if (userService.checkAdminRole(user).isSuccess()) {
			//填充我们增加产品的业务逻辑
			return productService.saveOrUpdateProduct(product);
		} else {
			return ServerResponse.error("无权限操作");
		}
	}

	@RequestMapping("set_sale_status.do")
	@ResponseBody
	public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");

		}
		if (userService.checkAdminRole(user).isSuccess()) {
			return productService.setSaleStatus(productId, status);
		} else {
			return ServerResponse.error("无权限操作");
		}
	}

	@RequestMapping("detail.do")
	@ResponseBody
	public ServerResponse getDetail(HttpSession session, Integer productId) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");

		}
		if (userService.checkAdminRole(user).isSuccess()) {
			//填充业务
			return productService.manageProductDetail(productId);

		} else {
			return ServerResponse.error("无权限操作");
		}
	}

	@RequestMapping("del_product.do")
	@ResponseBody
	public ServerResponse deleteProduct(HttpSession session, Integer productId) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");
		}
		if (userService.checkAdminRole(user).isSuccess()) {
			//填充业务
			return productService.deleteProduct(productId);
		} else {
			return ServerResponse.error("无权限操作");
		}
	}

	@RequestMapping("list.do")
	@ResponseBody
	public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");

		}
		if (userService.checkAdminRole(user).isSuccess()) {
			//填充业务
			return productService.getProductList(pageNum, pageSize);
		} else {
			return ServerResponse.error("无权限操作");
		}
	}

	@RequestMapping("search.do")
	@ResponseBody
	public ServerResponse productSearch(HttpSession session, String productName, Integer productId, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");

		}
		if (userService.checkAdminRole(user).isSuccess()) {
			//填充业务
			return productService.searchProduct(productName, productId, pageNum, pageSize);
		} else {
			return ServerResponse.error("无权限操作");
		}
	}

	@RequestMapping("upload.do")
	@ResponseBody
	public ServerResponse upload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");
		}
		if (userService.checkAdminRole(user).isSuccess()) {
			String path = request.getSession().getServletContext().getRealPath("upload");
			String targetFileName = fileService.upload(file, path);
			String url = Const.IMAGE_HOST + targetFileName;

			Map fileMap = Maps.newHashMap();
			fileMap.put("uri", targetFileName);
			fileMap.put("url", url);
			return ServerResponse.success(fileMap);
		} else {
			return ServerResponse.error("无权限操作");
		}
	}


	@RequestMapping("richtext_img_upload.do")
	@ResponseBody
	public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
		Map resultMap = Maps.newHashMap();
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			resultMap.put("success", false);
			resultMap.put("msg", "请登录管理员");
			return resultMap;
		}
		//富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
		if (userService.checkAdminRole(user).isSuccess()) {
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
		} else {
			resultMap.put("success", false);
			resultMap.put("msg", "无权限操作");
			return resultMap;
		}
	}


}
