package cn.nomeatcoder.common.query;

import java.util.Date;

import cn.nomeatcoder.common.QueryBase;
import lombok.Data;



/**
 * 查询类
 * @ClassName: PayInfoQuery
 * @author Chenzhe Mao
 * @Date 2020-01-31 14:38:45
 */
@Data
public class PayInfoQuery extends QueryBase {
	private Integer id;
    /**
    * 用户id
    */
	private Integer userId;
    /**
    * 订单号
    */
	private Long orderNo;
    /**
    * 支付平台:1-支付宝,2-微信
    */
	private Integer payPlatform;
    /**
    * 支付宝支付流水号
    */
	private String platformNumber;
    /**
    * 支付宝支付状态
    */
	private String platformStatus;
    /**
    * 创建时间
    */
	private Date createTime;
    /**
    * 更新时间
    */
	private Date updateTime;
}
