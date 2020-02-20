package cn.nomeatcoder.common.vo;

import cn.nomeatcoder.common.Domain;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 实体类
 *
 * @author Chenzhe Mao
 * @ClassName: User
 * @Date 2020-01-31 14:38:45
 */
@Data
public class UserVo implements Domain {
	/**
	 * 用户表id
	 */
	private Integer id;
	/**
	 * 用户名
	 */
	private String username;
	private String email;
	private String phone;
	/**
	 * 找回密码问题
	 */
	private String question;
	/**
	 * 找回密码答案
	 */
	private String answer;
	/**
	 * 角色0-管理员,1-普通用户
	 */
	private String role;
	/**
	 * 积分
	 */
	private String integral;
	/**
	 * 创建时间
	 */
	private String createTime;
	/**
	 * 最后一次更新时间
	 */
	private String updateTime;
}
