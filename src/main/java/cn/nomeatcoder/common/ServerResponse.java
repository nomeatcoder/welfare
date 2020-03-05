package cn.nomeatcoder.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class ServerResponse implements Serializable {

	private Integer status;
	private String msg;
	private Object data;

	public ServerResponse(Integer status, String msg, Object data) {
		this.status = status;
		this.msg = msg;
		this.data = data;
	}

	public static ServerResponse success(Object data) {
		return success("操作成功", data);
	}

	public static ServerResponse success(String msg) {
		return success(msg, null);
	}

	public static ServerResponse success(String msg, Object data) {
		return new ServerResponse(0, msg, data);
	}

	public static ServerResponse success() {
		return new ServerResponse(0, "操作成功", null);
	}

	public static ServerResponse error(String msg) {
		return error(1,msg);
	}

	public static ServerResponse error() {
		return error(1,"操作失败");
	}

	public static ServerResponse error(String msg, Object data) {
		return error(1, msg, data);
	}

	public static ServerResponse error(Integer status, String msg) {
		return new ServerResponse(status, msg, null);
	}

	public static ServerResponse error(Integer status, String msg, Object data) {
		return new ServerResponse(status, msg, data);
	}

	@JsonIgnore
	public boolean isSuccess(){
		return this.status.equals(ResponseCode.SUCCESS.getCode());
	}

}
