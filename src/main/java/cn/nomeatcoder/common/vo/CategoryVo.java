package cn.nomeatcoder.common.vo;

import cn.nomeatcoder.common.Domain;
import lombok.Data;

import java.util.Date;


/**
 * 实体类
 *
 * @author Chenzhe Mao
 * @ClassName: Category
 * @Date 2020-01-31 14:38:45
 */
@Data
public class CategoryVo implements Domain {
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
	 * 图片，二级分类必须有
	 */
	private String image;
}
