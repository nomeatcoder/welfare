package cn.nomeatcoder.common.error;

public class BizException extends RuntimeException {
	public BizException(String message){
		super(message);
	}
}
