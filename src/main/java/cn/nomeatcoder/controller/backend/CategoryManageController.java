package cn.nomeatcoder.controller.backend;


import cn.nomeatcoder.common.Const;
import cn.nomeatcoder.common.ResponseCode;
import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.domain.User;
import cn.nomeatcoder.service.CategoryService;
import cn.nomeatcoder.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("manage/category")
public class CategoryManageController {


	@Resource
	private UserService userService;

	@Resource
	private CategoryService categoryService;

	@RequestMapping("add_category.do")
	@ResponseBody
	public ServerResponse addCategory(HttpSession session,
	                                  String categoryName,
	                                  @RequestParam(value = "parentId", defaultValue = "0") int parentId,
	                                  String subImage) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
		}
		//校验一下是否是管理员
		if (userService.checkAdminRole(user).isSuccess()) {
			//是管理员
			//增加我们处理分类的逻辑
			return categoryService.addCategory(categoryName, parentId, subImage);

		} else {
			return ServerResponse.error("无权限操作,需要管理员权限");
		}
	}

    @RequestMapping("del_category.do")
    @ResponseBody
    public ServerResponse delCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") int categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        //校验一下是否是管理员
        if (userService.checkAdminRole(user).isSuccess()) {
            //是管理员
            //增加我们处理分类的逻辑
            return categoryService.delCategory(categoryId);

        } else {
            return ServerResponse.error("无权限操作,需要管理员权限");
        }
    }

	@RequestMapping("set_category_name.do")
	@ResponseBody
	public ServerResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
		}
		if (userService.checkAdminRole(user).isSuccess()) {
			//更新categoryName
			return categoryService.updateCategoryName(categoryId, categoryName);
		} else {
			return ServerResponse.error("无权限操作,需要管理员权限");
		}
	}

	@RequestMapping("get_category.do")
	@ResponseBody
	public ServerResponse getChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
		}
		if (userService.checkAdminRole(user).isSuccess()) {
			//查询子节点的category信息,并且不递归,保持平级
			return categoryService.getChildrenParallelCategory(categoryId);
		} else {
			return ServerResponse.error("无权限操作,需要管理员权限");
		}
	}

	@RequestMapping("get_deep_category.do")
	@ResponseBody
	public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
		}
		if (userService.checkAdminRole(user).isSuccess()) {
			return categoryService.selectCategoryAndChildrenById(categoryId);
		} else {
			return ServerResponse.error("无权限操作,需要管理员权限");
		}
	}


}
