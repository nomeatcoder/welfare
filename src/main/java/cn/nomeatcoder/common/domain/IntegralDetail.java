package cn.nomeatcoder.common.domain;


import cn.nomeatcoder.common.Domain;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体类
 * @ClassName: IntegralDetail
 * @author Chenzhe Mao
 * @Date 2020-02-21 18:26:12
 */
@Data
public class IntegralDetail implements Domain {
	private Integer id;
	/**
	* 用户id
	*/
	private Integer userId;
	/**
	* 用户名
	*/
	private String username;
	/**
	* 类型 0-充值 1-购物抵扣
	*/
	private Integer type;
	/**
	* 数额
	*/
	private BigDecimal num;
	/**
	* 剩余积分
	*/
	private BigDecimal remainIntegral;
	/**
	* 创建时间
	*/
	private Date createTime;
	/**
	* 更新时间
	*/
	private Date updateTime;
}
