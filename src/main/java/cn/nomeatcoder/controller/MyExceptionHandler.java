package cn.nomeatcoder.controller;

import cn.nomeatcoder.common.ResponseCode;
import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.exception.BizException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ServerResponse execptionHandler(Exception e) {
		if (e instanceof BizException) {
			return ServerResponse.error(ResponseCode.ERROR.getCode(), e.getMessage());
		}
		return ServerResponse.error(ResponseCode.SERVER_ERROR.getCode(), ResponseCode.SERVER_ERROR.getDesc());
	}

}
