package cn.nomeatcoder.common.domain;

import cn.nomeatcoder.common.Domain;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 实体类
 *
 * @author Chenzhe Mao
 * @ClassName: OrderItem
 * @Date 2020-01-31 14:38:45
 */
@Data
public class OrderItem implements Domain {
	/**
	 * 订单子表id
	 */
	private Integer id;
	private Integer userId;
	private Long orderNo;
	/**
	 * 商品id
	 */
	private Integer productId;
	/**
	 * 商品名称
	 */
	private String productName;
	/**
	 * 商品图片地址
	 */
	private String productImage;
	/**
	 * 生成订单时的商品单价，单位是元,保留两位小数
	 */
	private BigDecimal currentUnitPrice;
	/**
	 * 商品数量
	 */
	private Integer quantity;
	/**
	 * 商品总价,单位是元,保留两位小数
	 */
	private BigDecimal totalPrice;
	private Date createTime;
	private Date updateTime;
}
