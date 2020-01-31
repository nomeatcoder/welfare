package cn.nomeatcoder.domain;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Data
public class Cart {
    @Id
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    /**
     * 商品id
     */
    @Column(name = "product_id")
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
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

}