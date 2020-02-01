package cn.nomeatcoder.controller.protal;

import cn.nomeatcoder.common.Const;
import cn.nomeatcoder.common.ResponseCode;
import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.domain.User;
import cn.nomeatcoder.service.UserService;
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
			session.setAttribute(Const.CURRENT_USER, response.getData());
			return ServerResponse.success();
		}
		return response;
	}

	@PostMapping("logout.do")
	public ServerResponse logout(HttpSession session) {
		session.removeAttribute(Const.CURRENT_USER);
		return ServerResponse.success();
	}

	@PostMapping("register.do")
	public ServerResponse register(User user) {
		return userService.register(user);
	}

	@PostMapping("check_valid.do")
	public ServerResponse checkValid(String str, String type) {
		return userService.checkValid(str, type);
	}

	@PostMapping("get_user_info.do")
	public ServerResponse getUserInfo(HttpSession session) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user != null) {
			return ServerResponse.success(user);
		}
		return ServerResponse.error("用户未登录,无法获取当前用户的信息");
	}

	@PostMapping("forget_get_question.do")
	public ServerResponse forgetGetQuestion(String username) {
		return userService.selectQuestion(username);
	}

	@PostMapping("forget_check_answer.do")
	public ServerResponse forgetCheckAnswer(String username, String question, String answer) {
		return userService.checkAnswer(username, question, answer);
	}

	@PostMapping("forget_reset_password.do")
	public ServerResponse forgetRestPassword(String username, String passwordNew, String forgetToken) {
		return userService.forgetResetPassword(username, passwordNew, forgetToken);
	}

	@PostMapping("reset_password.do")
	public ServerResponse resetPassword(HttpSession session, String passwordOld, String passwordNew) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.error("用户未登录");
		}
		return userService.resetPassword(passwordOld, passwordNew, user);
	}


	@PostMapping("update_information.do")
	public ServerResponse update_information(HttpSession session, User user) {
		User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
		if (currentUser == null) {
			return ServerResponse.error("用户未登录");
		}
		user.setId(currentUser.getId());
		user.setUsername(currentUser.getUsername());
		ServerResponse response = userService.updateInformation(user, currentUser);
		if (response.isSuccess()) {
			currentUser.setAnswer(user.getAnswer());
			currentUser.setPhone(user.getPhone());
			currentUser.setQuestion(user.getQuestion());
			currentUser.setEmail(user.getEmail());
			session.setAttribute(Const.CURRENT_USER, currentUser);
		}
		return response;
	}

	@PostMapping("get_information.do")
	public ServerResponse get_information(HttpSession session) {
		User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
		if (currentUser == null) {
			return new ServerResponse(ResponseCode.NEED_LOGIN.getCode(), "未登录,需要强制登录", null);
		}
		return userService.getInformation(currentUser.getId());
	}

}
