package cn.nomeatcoder.common.query;

import java.math.BigDecimal;
import java.util.Date;

import cn.nomeatcoder.common.QueryBase;
import lombok.Data;



/**
 * 查询类
 * @ClassName: UserQuery
 * @author Chenzhe Mao
 * @Date 2020-01-31 14:38:45
 */
@Data
public class UserQuery extends QueryBase {
    /**
    * 用户表id
    */
	private Integer id;
    /**
    * 用户名
    */
	private String username;
    /**
    * 用户密码，MD5加密
    */
	private String password;
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
	private Integer role;
	/**
	 * 积分
	 */
	private BigDecimal integral;
    /**
    * 创建时间
    */
	private Date createTime;
    /**
    * 最后一次更新时间
    */
	private Date updateTime;
}
