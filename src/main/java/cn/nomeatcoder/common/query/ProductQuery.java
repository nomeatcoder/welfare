package cn.nomeatcoder.common.query;

import cn.nomeatcoder.common.QueryBase;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 查询类
 * @ClassName: ProductQuery
 * @author Chenzhe Mao
 * @Date 2020-01-31 14:38:45
 */
@Data
public class ProductQuery extends QueryBase {
    /**
    * 商品id
    */
	private Integer id;
    /**
    * 分类id,对应category表的主键
    */
	private Integer categoryId;
    /**
    * 商品名称
    */
	private String name;
    /**
    * 商品副标题
    */
	private String subtitle;
    /**
    * 产品主图,url相对地址
    */
	private String mainImage;
    /**
    * 图片地址,json格式,扩展用
    */
	private String subImages;
    /**
    * 商品详情
    */
	private String detail;
    /**
    * 价格,单位-元保留两位小数
    */
    private BigDecimal price;
    /**
    * 库存数量
    */
	private Integer stock;
    /**
    * 商品状态.1-在售 2-下架 3-删除
    */
	private Integer status;
    /**
    * 创建时间
    */
	private Date createTime;
    /**
    * 更新时间
    */
	private Date updateTime;
	/**
	 * in
	 */
	private List<Integer> categoryIdList;
}
