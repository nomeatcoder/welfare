package cn.nomeatcoder.common;

import lombok.Data;

import java.util.List;

@Data
public class PageInfo<T> {
	//当前页
	private int pageNum = 0;
	//每页大小
	private int pageSize = 0;
	//总数
	private long total = 0;
	//总页数
	private int pages = 0;
	//数据列表
	private List<T> list;
	//上一页
	private int prePage = 0;
	//下一页
	private int nextPage = 0;
	//是否有前一页
	private boolean hasPreviousPage = false;
	//是否有后一页
	private boolean hasNextPage= false;

	public void init(int pageNum, int pageSize, List<T> list) {
		this.total = list.size();
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.list = list;
		this.pages = (int) ((this.total + pageSize - 1)/ pageSize);
		if(pageNum > 1){
			this.prePage = pageNum -1;
			this.hasPreviousPage = true;
		}
		if(pageNum<this.pages){
			this.nextPage = pageNum+1;
			this.hasNextPage = true;
		}
	}
}
