package cn.nomeatcoder.common.vo;

import cn.nomeatcoder.common.domain.Category;
import lombok.Data;

import java.util.List;

@Data
public class CategoryDetailVo {
	/**
	 * 一级分类id
	 */
	private Integer parentId;
	/**
	 * 一级分类名
	 */
	private String name;
	/**
	 * 二级分类列表
	 */
	private List<CategoryVo> subList;
}
