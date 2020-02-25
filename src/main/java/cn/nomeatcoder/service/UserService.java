package cn.nomeatcoder.service;

import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.domain.User;

import java.math.BigDecimal;

public interface UserService {

	ServerResponse login(String username, String password);

	ServerResponse register(User user);

	ServerResponse checkValid(String str, String type);

	ServerResponse selectQuestion(String username);

	ServerResponse checkAnswer(String username, String question, String answer);

	ServerResponse forgetResetPassword(String username, String passwordNew, String forgetToken);

	ServerResponse resetPassword(String passwordOld, String passwordNew, User user);

	ServerResponse updateInformation(User user, User currentUser);

	ServerResponse getInformation(Integer userId);

	ServerResponse list(int pageSize, int pageNum);

	ServerResponse search(String username, int pageSize, int pageNum);

	ServerResponse charge(int id, String integral);

	ServerResponse insertIntegralDetail(User user, int type, BigDecimal integral, BigDecimal remainIntegral);

	ServerResponse integralList(int pageSize, int pageNum);

	ServerResponse integralSearch(String username, int pageSize, int pageNum);
}
