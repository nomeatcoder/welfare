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
		return new ServerResponse(0, "操作成功", data);
	}

	public static ServerResponse success() {
		return new ServerResponse(0, "操作成功", null);
	}

	public static ServerResponse error(String msg) {
		return new ServerResponse(1, msg, null);
	}

	@JsonIgnore
	public boolean isSuccess(){
		return this.status.equals(ResponseCode.SUCCESS.getCode());
	}

}
