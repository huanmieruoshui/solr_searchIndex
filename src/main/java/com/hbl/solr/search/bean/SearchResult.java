package com.hbl.solr.search.bean;

import java.util.List;

import com.hbl.solr.bean.Product;

public class SearchResult {
	/**查询耗时**/
	private long timeConsumer;
	
	private List<Product> results;
	
	private long rowCount;
	
	private int pagesCount;

	private boolean error = false;

	public List<Product> getResults() {
		return results;
	}

	public void setResults(List<Product> results) {
		this.results = results;
	}

	public long getRowCount() {
		return rowCount;
	}

	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}

	public int getPagesCount() {
		return pagesCount;
	}

	public void setPagesCount(int pagesCount) {
		this.pagesCount = pagesCount;
	}

	public long getTimeConsumer() {
		return timeConsumer;
	}

	public void setTimeConsumer(long timeConsumer) {
		this.timeConsumer = timeConsumer;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}
	
}
