package cn.nomeatcoder.common.vo;

import lombok.Data;

import java.util.List;

@Data
public class IndexVo {
	private String imageHost;
	private List<CategoryDetailVo> list;
}
