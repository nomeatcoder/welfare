package cn.nomeatcoder.common.query;

import java.util.Date;

import cn.nomeatcoder.common.QueryBase;
import lombok.Data;



/**
 * 查询类
 * @ClassName: CategoryQuery
 * @author Chenzhe Mao
 * @Date 2020-01-31 14:38:45
 */
@Data
public class CategoryQuery extends QueryBase {
    /**
    * 类别Id
    */
	private Integer id;
    /**
    * 父类别id当id=0时说明是根节点,一级类别
    */
	private Integer parentId;
    /**
    * 类别名称
    */
	private String name;
    /**
    * 类别状态1-正常,2-已废弃
    */
	private Boolean status;
    /**
    * 排序编号,同类展示顺序,数值相等则自然排序
    */
	private Integer sortOrder;
    /**
    * 创建时间
    */
	private Date createTime;
    /**
    * 更新时间
    */
	private Date updateTime;
}
