package cn.nomeatcoder.controller.backend;

import cn.nomeatcoder.common.Const;
import cn.nomeatcoder.common.ResponseCode;
import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.domain.User;
import cn.nomeatcoder.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;

@RestController
@RequestMapping("manage/user")
public class UserManageController {

	@Resource
	private UserService userService;

	@RequestMapping("login.do")
	@ResponseBody
	public ServerResponse login(String username, String password, HttpSession session) {
		ServerResponse response = userService.login(username, password);
		if (response.isSuccess()) {
			User user = (User) response.getData();
			if (user.getRole() == Const.Role.ROLE_ADMIN) {
				//说明登录的是管理员
				session.setAttribute(Const.CURRENT_USER, user);
				return response;
			} else {
				return ServerResponse.error("不是管理员,无法登录");
			}
		}
		return response;
	}

	@RequestMapping("list.do")
	@ResponseBody
	public ServerResponse userList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
	                                @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");
		}
		return userService.list(pageSize,pageNum);
	}

	@RequestMapping("search.do")
	@ResponseBody
	public ServerResponse search(HttpSession session,
	                             @RequestParam(value = "username")String username,
	                             @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
	                               @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");
		}
		return userService.search(username,pageSize,pageNum);
	}

	@RequestMapping("charge.do")
	@ResponseBody
	public ServerResponse search(HttpSession session,
	                             @RequestParam(value = "id",defaultValue = "0")int id,
	                             @RequestParam(value = "integral",defaultValue = "0") String integral){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");
		}
		if(StringUtils.isBlank(integral)){
			return ServerResponse.error("请输入正确的积分格式");
		}
		try {
			Double.valueOf(integral);
		} catch (NumberFormatException e) {
			return ServerResponse.error("请输入正确的积分格式");
		}
		return userService.charge(id,integral);
	}

}
