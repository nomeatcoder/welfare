package cn.nomeatcoder.controller.protal;

import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Slf4j
@Controller
@RequestMapping("index")
public class IndexController {

	@Resource
	private CategoryService categoryService;

	@RequestMapping("get_index.do")
	@ResponseBody
	public ServerResponse getIndexInfo(){
		return categoryService.getIndex();
	}
}
