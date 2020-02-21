package cn.nomeatcoder.common.query;

import java.math.BigDecimal;
import java.util.Date;

import cn.nomeatcoder.common.QueryBase;
import lombok.Data;

/**
 * 查询类
 * @ClassName: IntegralDetailQuery
 * @author Chenzhe Mao
 * @Date 2020-02-21 18:26:12
 */
@Data
public class IntegralDetailQuery extends QueryBase {
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
