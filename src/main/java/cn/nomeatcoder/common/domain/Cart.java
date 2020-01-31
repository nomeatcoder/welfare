package cn.nomeatcoder.common.domain;

import cn.nomeatcoder.common.Domain;
import lombok.Data;

import java.util.Date;


/**
 * 实体类
 *
 * @author Chenzhe Mao
 * @ClassName: Cart
 * @Date 2020-01-31 14:38:45
 */
@Data
public class Cart implements Domain {
	private Integer id;
	private Integer userId;
	/**
	 * 商品id
	 */
	private Integer productId;
	/**
	 * 数量
	 */
	private Integer quantity;
	/**
	 * 是否选择,1=已勾选,0=未勾选
	 */
	private Integer checked;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
}
