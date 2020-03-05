package cn.nomeatcoder.service.impl;

import cn.nomeatcoder.common.*;
import cn.nomeatcoder.common.domain.IntegralDetail;
import cn.nomeatcoder.common.domain.User;
import cn.nomeatcoder.common.exception.BizException;
import cn.nomeatcoder.common.query.IntegralDetailQuery;
import cn.nomeatcoder.common.query.UserQuery;
import cn.nomeatcoder.common.vo.IntegralDetailVo;
import cn.nomeatcoder.common.vo.UserVo;
import cn.nomeatcoder.dal.mapper.IntegralDetailMapper;
import cn.nomeatcoder.dal.mapper.UserMapper;
import cn.nomeatcoder.service.UserService;
import cn.nomeatcoder.utils.BigDecimalUtils;
import cn.nomeatcoder.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service("userService")
public class UserServiceImpl implements UserService {

	@Resource
	private UserMapper userMapper;

	@Resource
	private CommonProperties commonProperties;

	@Resource
	private IntegralDetailMapper integralDetailMapper;

	@Resource
	private MyCache myCache;

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
					return ServerResponse.error("用户名已存在", user);
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
			return ServerResponse.success((Object) question);
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
			myCache.setKey(MyCache.TOKEN_PREFIX + username, forgetToken);
			return ServerResponse.success((Object) forgetToken);
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
		String token = myCache.getKey(MyCache.TOKEN_PREFIX + username);
		if (StringUtils.isBlank(token)) {
			return ServerResponse.error("token无效或者过期");
		}

		if (StringUtils.equals(forgetToken, token)) {
			String md5Password = MD5Utils.MD5EncodeUtf8(passwordNew, commonProperties.getSalt());
			User user = new User();
			if (validResponse.getData() != null) {
				user.setId(((User) validResponse.getData()).getId());
			}
			user.setUsername(username);
			user.setPassword(md5Password);
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
	public ServerResponse list(int pageSize, int pageNum) {
		return search(null, pageSize, pageNum);
	}

	@Override
	public ServerResponse search(String username, int pageSize, int pageNum) {
		UserQuery query = new UserQuery();
		query.setPageSize(pageSize);
		query.setCurrentPage(pageNum);
		query.setUsername(username);
		List<User> list = userMapper.find(query);
		List<UserVo> userVoList = list.stream().map(
			v -> {
				UserVo userVo = new UserVo();
				userVo.setId(v.getId());
				userVo.setUsername(v.getUsername());
				userVo.setEmail(v.getEmail());
				userVo.setPhone(v.getPhone());
				userVo.setQuestion(v.getQuestion());
				userVo.setAnswer(v.getAnswer());
				userVo.setRole(v.getRole() == 1 ? "管理员" : "普通用户");
				userVo.setIntegral(v.getIntegral().toString());
				userVo.setCreateTime(Const.DF.format(v.getCreateTime()));
				userVo.setUpdateTime((Const.DF.format(v.getUpdateTime())));
				return userVo;
			}
		).collect(Collectors.toList());
		PageInfo pageInfo = new PageInfo();
		pageInfo.init(userMapper.count(query), pageNum, pageSize, userVoList);
		return ServerResponse.success(pageInfo);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public ServerResponse charge(int id, String integral) {
		UserQuery query = new UserQuery();
		query.setId(id);
		User user = userMapper.get(query);
		if(user == null){
			return ServerResponse.error("用户不存在");
		}
		BigDecimal remainIntegral = BigDecimalUtils.add(user.getIntegral(), new BigDecimal(integral));
		user.setIntegral(remainIntegral);
		int rowCount = userMapper.update(user);
		if (rowCount > 0) {
			ServerResponse serverResponse = insertIntegralDetail(user, 0, new BigDecimal(integral), remainIntegral);
			if (serverResponse.isSuccess()) {
				return ServerResponse.success("充值成功");
			}
			throw new BizException("插入积分详情失败");
		}
		return ServerResponse.error("充值失败");
	}

	@Override
	public ServerResponse insertIntegralDetail(User user, int type, BigDecimal integral, BigDecimal remainIntegral) {
		IntegralDetail integralDetail = new IntegralDetail();
		integralDetail.setUserId(user.getId());
		integralDetail.setUsername(user.getUsername());
		integralDetail.setType(type);
		integralDetail.setNum(integral);
		integralDetail.setRemainIntegral(remainIntegral);
		long rowCount = integralDetailMapper.insert(integralDetail);
		if (rowCount > 0) {
			return ServerResponse.success();
		}
		return ServerResponse.error();
	}

	@Override
	public ServerResponse integralList(int pageSize, int pageNum) {
		return integralSearch(null, pageSize, pageNum);
	}

	@Override
	public ServerResponse integralSearch(String username, int pageSize, int pageNum) {
		IntegralDetailQuery query = new IntegralDetailQuery();
		query.setPageSize(pageSize);
		query.setCurrentPage(pageNum);
		query.setUsername(username);
		query.putOrderBy("id", false);
		query.setOrderByEnable(true);
		List<IntegralDetail> list = integralDetailMapper.find(query);
		List<IntegralDetailVo> integralDetailVoList = list.stream().map(
			v -> {
				IntegralDetailVo integralDetailVo = new IntegralDetailVo();
				integralDetailVo.setId(v.getId());
				integralDetailVo.setUsername(v.getUsername());
				integralDetailVo.setType(v.getType() == 0 ? "积分充值" : (v.getType() == 1 ? "购物抵扣" : "关单退回"));
				integralDetailVo.setAdd(v.getType() == 1 ? false : true);
				integralDetailVo.setNum(v.getType() == 1 ? ("-" + v.getNum().toString()) : ("+" + v.getNum().toString()));
				integralDetailVo.setRemainIntegral(v.getRemainIntegral().toString());
				integralDetailVo.setCreateTime(Const.DF.format(v.getCreateTime()));
				return integralDetailVo;
			}
		).collect(Collectors.toList());
		PageInfo pageInfo = new PageInfo();
		pageInfo.init(integralDetailMapper.count(query), pageNum, pageSize, integralDetailVoList);
		return ServerResponse.success(pageInfo);
	}
}
