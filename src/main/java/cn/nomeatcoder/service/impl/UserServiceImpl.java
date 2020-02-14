package cn.nomeatcoder.service.impl;

import cn.nomeatcoder.common.CommonProperties;
import cn.nomeatcoder.common.Const;
import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.TokenCache;
import cn.nomeatcoder.common.domain.User;
import cn.nomeatcoder.common.query.UserQuery;
import cn.nomeatcoder.dal.mapper.UserMapper;
import cn.nomeatcoder.service.UserService;
import cn.nomeatcoder.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

@Slf4j
@Service("userService")
public class UserServiceImpl implements UserService {

	@Resource
	private UserMapper userMapper;

	@Resource
	private CommonProperties commonProperties;

	@Override
	public ServerResponse login(String username, String password) {
		UserQuery query = new UserQuery();
		query.setUsername(username);
		User user = userMapper.get(query);
		if (user == null) {
			return ServerResponse.error("用户名不存在");
		}
		String md5Password = MD5Utils.MD5EncodeUtf8(password, commonProperties.getSalt());
		if(!md5Password.equals(user.getPassword())){
			return ServerResponse.error("密码错误");
		}

		return ServerResponse.success(user);
	}

	@Override
	public ServerResponse register(User user) {
		ServerResponse validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
		if (!validResponse.isSuccess()) {
			return validResponse;
		}
		validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
		if (!validResponse.isSuccess()) {
			return validResponse;
		}
		user.setRole(Const.Role.ROLE_CUSTOMER);
		//MD5加密
		user.setPassword(MD5Utils.MD5EncodeUtf8(user.getPassword(), commonProperties.getSalt()));
		long resultCount = userMapper.insert(user);
		if (resultCount == 0) {
			return ServerResponse.error("注册失败");
		}
		return ServerResponse.success();
	}

	@Override
	public ServerResponse checkValid(String str, String type) {
		if (StringUtils.isNotBlank(type)) {
			//开始校验
			if (Const.USERNAME.equals(type)) {
				UserQuery query = new UserQuery();
				query.setUsername(str);
				User user = userMapper.get(query);
				if (user != null) {
					return ServerResponse.error("用户名已存在");
				}
			}
			if (Const.EMAIL.equals(type)) {
				UserQuery query = new UserQuery();
				query.setEmail(str);
				User user = userMapper.get(query);
				if (user != null) {
					return ServerResponse.error("email已存在");
				}
			}
		} else {
			return ServerResponse.error("参数错误");
		}
		return ServerResponse.success();
	}

	@Override
	public ServerResponse selectQuestion(String username) {
		UserQuery query = new UserQuery();
		query.setUsername(username);
		User user = userMapper.get(query);
		if (user == null) {
			return ServerResponse.error("用户不存在");
		}
		String question = user.getQuestion();
		if (StringUtils.isNotBlank(question)) {
			return ServerResponse.success(question);
		}
		return ServerResponse.error("找回密码的问题是空的");
	}

	@Override
	public ServerResponse checkAnswer(String username, String question, String answer) {
		UserQuery query = new UserQuery();
		query.setUsername(username);
		query.setQuestion(question);
		query.setAnswer(answer);
		User user = userMapper.get(query);
		if (user != null) {
			String forgetToken = UUID.randomUUID().toString();
			TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
			return ServerResponse.success(forgetToken);
		}
		return ServerResponse.error("问题的答案错误");
	}

	@Override
	public ServerResponse forgetResetPassword(String username, String passwordNew, String forgetToken) {
		if (StringUtils.isBlank(forgetToken)) {
			return ServerResponse.error("参数错误,token需要传递");
		}
		ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
		if (validResponse.isSuccess()) {
			//用户不存在
			return ServerResponse.error("用户不存在");
		}
		String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
		if (StringUtils.isBlank(token)) {
			return ServerResponse.error("token无效或者过期");
		}

		if (StringUtils.equals(forgetToken, token)) {
			String md5Password = MD5Utils.MD5EncodeUtf8(passwordNew, commonProperties.getSalt());
			User user = new User();
			user.setUsername(username);
			user.setPassword(passwordNew);
			int rowCount = userMapper.update(user);
			if (rowCount > 0) {
				return ServerResponse.success("修改密码成功");
			}
		} else {
			return ServerResponse.error("token错误,请重新获取重置密码的token");
		}
		return ServerResponse.error("修改密码失败");
	}

	@Override
	public ServerResponse resetPassword(String passwordOld, String passwordNew, User user) {
		if (!user.getPassword().equals(MD5Utils.MD5EncodeUtf8(passwordOld, commonProperties.getSalt()))) {
			return ServerResponse.error("旧密码错误");
		}

		user.setPassword(MD5Utils.MD5EncodeUtf8(passwordNew, commonProperties.getSalt()));
		int updateCount = userMapper.update(user);
		if (updateCount > 0) {
			return ServerResponse.success("密码更新成功");
		}
		return ServerResponse.error("密码更新失败");
	}

	@Override
	public ServerResponse updateInformation(User user, User currentUser) {
		ServerResponse checkValid = checkValid(user.getEmail(), Const.EMAIL);
		if (!checkValid.isSuccess() && !user.getEmail().equals(currentUser.getEmail())) {
			return ServerResponse.error("email已存在,请更换email再尝试更新");
		}
		User updateUser = new User();
		updateUser.setId(user.getId());
		updateUser.setEmail(user.getEmail());
		updateUser.setPhone(user.getPhone());
		updateUser.setQuestion(user.getQuestion());
		updateUser.setAnswer(user.getAnswer());

		int updateCount = userMapper.update(updateUser);
		if (updateCount > 0) {
			return ServerResponse.success();
		}
		return ServerResponse.error("更新个人信息失败");
	}

	@Override
	public ServerResponse getInformation(Integer userId) {
		UserQuery query = new UserQuery();
		query.setId(userId);
		User user = userMapper.get(query);
		if (user == null) {
			return ServerResponse.error("找不到当前用户");
		}
		return ServerResponse.success(user);
	}

	@Override
	public ServerResponse checkAdminRole(User user) {
		if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
			return ServerResponse.success();
		}
		return ServerResponse.error();
	}
}
