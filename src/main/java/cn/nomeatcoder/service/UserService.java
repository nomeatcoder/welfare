package cn.nomeatcoder.service;

import cn.nomeatcoder.common.ServerResponse;

public interface UserService {

	ServerResponse login(String username, String password);

}
