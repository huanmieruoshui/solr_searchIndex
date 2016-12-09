package com.hbl.solr.search.bean;

import java.io.Serializable;

public class SearchParam implements Serializable{

	private static final long serialVersionUID = 1L;

	/**查询关键词 多个关键词用 空格隔开**/
	private String q; 
	/**articleIsPublished=1发布的文章, articleIsPublished=0未发布的文章**/
	private int articleIsPublished =1; 
	/**起始页，如果没有，默认为第一页**/
	private int s =1;
	/**每页显示多少条数据，默认10*/
	private int n =10;
	/**数据返回格式，支持xml和json和java对象，默认Json**/
	private String wt="json";
	/**如果需要排序，排序的字段asc和desc排序，比如price asc或者price desc**/
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
