package cn.nomeatcoder.controller.protal;

import cn.nomeatcoder.common.ServerResponse;

import cn.nomeatcoder.service.UserService;
import com.google.gson.Gson;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("user")
public class UserController {

	@Resource
	private UserService userService;

	@PostMapping("login.do")
	public ServerResponse login(String username, String password, HttpSession session) {
		Assert.hasText(username, "username is invalid");
		Assert.hasText(password, "password is invalid");
		ServerResponse response = userService.login(username, password);
		if(response.isSuccess()){
			session.setAttribute(username,new Gson().toJson(response.getData()));
			return ServerResponse.success();
		}
		return response;
	}
}
