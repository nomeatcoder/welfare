package cn.nomeatcoder.service.impl;

import cn.nomeatcoder.common.Query;
import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.domain.User;
import cn.nomeatcoder.common.query.UserQuery;
import cn.nomeatcoder.dal.mapper.UserMapper;
import cn.nomeatcoder.service.UserService;
import cn.nomeatcoder.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Value("${password.salt}")
	private String salt;

	@Resource
	private UserMapper userMapper;

	@Override
	public ServerResponse login(String username, String password) {
		UserQuery query = new UserQuery();
		query.setUsername(username);
		User user = userMapper.get(query);
		if (user == null) {
			return ServerResponse.error("用户名不存在");
		}
		String md5Password = MD5Utils.MD5EncodeUtf8(password, salt);
		if(!md5Password.equals(user.getPassword())){
			return ServerResponse.error("密码错误");
		}
		user.setPassword(null);
		return ServerResponse.success(user);
	}
}
