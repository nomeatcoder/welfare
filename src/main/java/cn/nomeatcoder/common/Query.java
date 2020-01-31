package cn.nomeatcoder.common;

public interface Query {
	boolean isPageEnable();

	Long getStartRow();

	Long getCurrentPage();

	Long getPageSize();

	Long getTotalItem();

	void putTotalItem(Long totalItem);
}
