package cn.nomeatcoder.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.*;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QueryBase implements Query, Serializable {
	private static final long serialVersionUID = 3258128059449226041L;
	private Boolean pageEnable = Boolean.TRUE;
	private Boolean orderByEnable;
	private Long pageSize;
	private Long currentPage;
	@JsonIgnore
	@Getter
	private Long totalItem;
	@Getter
	private List<OrderBy> orderByList;
	private Long startRow;
	private Long endRow;

	public void putOrderBy(String column, Boolean asc) {
		if (StringUtils.isBlank(column) || asc == null) {
			return;
		}
		if (orderByList == null) {
			orderByList = new ArrayList<OrderBy>();
		}
		orderByList.add(new OrderBy(column, asc ? "asc" : "desc"));
	}

	public void putTotalItem(int totalItem) {
		putTotalItem(Long.valueOf(totalItem));
	}

	public void putTotalItem(long totalItem) {
		putTotalItem(Long.valueOf(totalItem));
	}

	public void putTotalItem(Integer totalItem) {
		if (totalItem != null) {
			putTotalItem(Long.valueOf(totalItem));
		}
	}

	@Override
	public void putTotalItem(Long totalItem) {
		this.totalItem = totalItem;
		if (isPageEnable()) {
			initPage();
		}
	}

	public void setTotalItem(int totalItem) {
		putTotalItem(totalItem);
	}

	public void setTotalItem(Integer totalItem) {
		putTotalItem(totalItem);
	}

	public void setTotalItem(long totalItem) {
		putTotalItem(totalItem);
	}

	public void setTotalItem(Long totalItem) {
		putTotalItem(totalItem);
	}

	protected int getDefaultPageSize() {
		return 100000;
	}

	@Override
	public Long getPageSize() {
		if (isPageEnable()) {
			initPage();
			return pageSize;
		}
		return null;
	}

	public void setPageSizeInt(int pageSize) {
		setPageSize(Long.valueOf(pageSize));
	}

	public void setPageSize(long pageSize) {
		setPageSize(Long.valueOf(pageSize));
	}

	public void setPageSizeInteger(Integer pageSize) {
		if (pageSize != null) {
			setPageSize(Long.valueOf(pageSize));
		}
	}

	public void setPageSizeString(String pageSize) {
		setPageSize(getNumber(pageSize));
	}

	public void setPageSize(Long pageSize) {
		this.pageSize = pageSize;
		if (isPageEnable()) {
			initPage();
		}
	}

	@Override
	public Long getCurrentPage() {
		if (isPageEnable()) {
			initPage();
			return currentPage;
		}
		return null;
	}

	public void setCurrentPageInt(int currentPage) {
		setCurrentPage(Long.valueOf(currentPage));
	}

	public void setCurrentPage(long currentPage) {
		setCurrentPage(Long.valueOf(currentPage));
	}

	public void setCurrentPageInteger(Integer currentPage) {
		if (currentPage != null) {
			setCurrentPage(Long.valueOf(currentPage));
		}
	}

	public void setCurrentPageString(String currentPage) {
		this.setCurrentPage(getNumber(currentPage));
	}

	public void setCurrentPage(Long currentPage) {
		this.currentPage = currentPage;
		if (isPageEnable()) {
			initPage();
		}
	}

	@Override
	public Long getStartRow() {
		if (isPageEnable()) {
			initPage();
			return startRow;
		}
		return null;
	}

	public Long getEndRow() {
		if (isPageEnable()) {
			initPage();
			return endRow;
		}
		return null;
	}

	private void initPage() {
		if (pageSize == null || pageSize.longValue() < 1L) {
			pageSize = Long.valueOf(getDefaultPageSize());
		}
		if (currentPage == null || currentPage.longValue() < 1L) {
			currentPage = Long.valueOf(1);
		}
		if (totalItem == null || totalItem.longValue() < 1L) {
			totalItem = Long.MAX_VALUE;
		}
		// long totalPage = totalItem.longValue() / pageSize.longValue();
		// if (totalPage * pageSize.longValue() < totalItem.longValue()) {
		// totalPage = totalPage + 1;
		// }
		// if (currentPage.longValue() > totalPage) {
		// currentPage = Long.valueOf(totalPage);
		// }
		startRow = Long.valueOf(pageSize.longValue() * (currentPage.longValue() - 1L));
		endRow = Long.valueOf(startRow.longValue() + pageSize.longValue() - 1);
	}

	private Long getNumber(String str) {
		if (str == null || "".equals(str.trim())) {
			return null;
		}
		try {
			return Long.valueOf(str);
		} catch (NumberFormatException ignore) {
		}
		return null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public int compareTo(Object o) {
		return CompareToBuilder.reflectionCompare(this, o);
	}

	@Override
	public boolean isPageEnable() {
		return pageEnable == null ? true : pageEnable.booleanValue();
	}

	public void setPageEnable(boolean pageEnable) {
		this.pageEnable = pageEnable;
	}

	public boolean isOrderByEnable() {
		return orderByEnable == null ? false : orderByEnable.booleanValue();
	}

	public void setOrderByEnable(boolean orderByEnable) {
		this.orderByEnable = orderByEnable;
	}

	@Data
	@AllArgsConstructor
	public class OrderBy {
		private String column;
		private String sort;
	}

}

