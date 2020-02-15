package cn.nomeatcoder.common.query;


import cn.nomeatcoder.common.QueryBase;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 查询类
 *
 * @author Chenzhe Mao
 * @ClassName: CartQuery
 * @Date 2020-01-31 14:38:45
 */
@Data
public class CartQuery extends QueryBase {
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
	/**
	 * 产品id列表
	 */
	private List<Integer> productIdList;
}
