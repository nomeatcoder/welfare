package cn.nomeatcoder.controller.backend;


import cn.nomeatcoder.common.ServerResponse;
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
	private CategoryService categoryService;

	@RequestMapping("save_category.do")
	@ResponseBody
	public ServerResponse saveCategory(HttpSession session,
	                                  Integer categoryId,
	                                  String categoryName,
	                                  @RequestParam(value = "parentId", defaultValue = "0") Integer parentId,
	                                  String subImage) {
		return categoryService.saveOrUpdateCategory(categoryId, categoryName, parentId, subImage);
	}

    @RequestMapping("del_category.do")
    @ResponseBody
    public ServerResponse delCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") int categoryId) {
		return categoryService.delCategory(categoryId);
    }

	@RequestMapping("set_category_name.do")
	@ResponseBody
	public ServerResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
		return categoryService.updateCategoryName(categoryId, categoryName);
	}

	@RequestMapping("get_category.do")
	@ResponseBody
	public ServerResponse getChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
		return categoryService.getChildrenParallelCategory(categoryId);
	}

	@RequestMapping("get_category_detail.do")
	@ResponseBody
	public ServerResponse getCategoryDetail(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
		return categoryService.getCategoryDetail(categoryId);
	}

	@RequestMapping("get_deep_category.do")
	@ResponseBody
	public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
		return categoryService.selectCategoryAndChildrenById(categoryId);
	}


}
