package cn.nomeatcoder.common.vo;


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
public class IntegralDetailVo implements Domain {
	private Integer id;
	/**
	* 用户名
	*/
	private String username;
	/**
	* 类型 0-充值 1-购物抵扣 2-关单退回
	*/
	private String type;
	private boolean add;
	/**
	* 数额
	*/
	private String num;
	/**
	* 剩余积分
	*/
	private String remainIntegral;
	/**
	* 创建时间
	*/
	private String createTime;

}
