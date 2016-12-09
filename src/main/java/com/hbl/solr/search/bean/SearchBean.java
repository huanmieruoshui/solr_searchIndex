package com.hbl.solr.search.bean;
/**
* @className:SearchBean.java
* @classDescription:
* @author:hbl
* @createTime:
*/
public class SearchBean {

	private String q;
	private int articleIsPublished;
	private int s;
	private int n;
	private String wt;
	private String sort;
	public String getQ() {
		return q;
	}
	public void setQ(String q) {
		this.q = q;
	}
	public int getArticleIsPublished() {
		return articleIsPublished;
	}
	public void setArticleIsPublished(int articleIsPublished) {
		this.articleIsPublished = articleIsPublished;
	}
	public int getS() {
		return s;
	}
	public void setS(int s) {
		this.s = s;
	}
	public int getN() {
		return n;
	}
	public void setN(int n) {
		this.n = n;
	}
	public String getWt() {
		return wt;
	}
	public void setWt(String wt) {
		this.wt = wt;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	
}
