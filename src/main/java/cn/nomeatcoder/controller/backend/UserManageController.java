package cn.nomeatcoder.controller.backend;

import cn.nomeatcoder.common.Const;
import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.domain.User;
import cn.nomeatcoder.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("manage/user")
public class UserManageController {

	@Resource
	private UserService userService;

	@PostMapping("login.do")
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

}
