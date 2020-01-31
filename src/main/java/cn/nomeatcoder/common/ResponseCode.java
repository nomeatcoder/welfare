package cn.nomeatcoder.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
	SUCCESS(0, "SUCCESS"),

	ERROR(1, "ERROR"),

	NEED_LOGIN(10, "NEED_LOGIN"),

	ILLEGAL_ARGUMENT(2, "ILLEGAL_ARGUMENT");

	private Integer code;
	private String desc;
}
