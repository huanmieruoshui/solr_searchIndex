package com.hbl.solr.test;


import java.util.List;

import com.hbl.solr.bean.Product;
import com.hbl.solr.search.SolrQuery;
import com.hbl.solr.search.bean.SearchBean;
import com.hbl.solr.search.bean.SearchResult;

import net.sf.json.JSONObject;

public class TestQueryNew {

	public static void main(String[] args) {

		//String param = "{\"n\":20,\"q\":\"spark\",\"s\":1,\"sort\":\"articleUpdateDate desc\",\"wt\":\"json\"}";
		//String host = "http://115.29.109.120/search/search";
		String host = "http://localhost:8090/search/search";
		SearchBean searchBean = new SearchBean();
		searchBean.setQ("hadoop安装文档");
		searchBean.setArticleIsPublished(1);
		searchBean.setN(20);
		searchBean.setS(1);
		//searchBean.setSort("articleUpdateDate desc");
		searchBean.setWt("json");
		SolrQuery solrQuery = new SolrQuery();
		JSONObject obj = JSONObject.fromObject(searchBean);
		SearchResult result = solrQuery.getSolrQuery(obj.toString(), host);
		List<Product> list = result.getResults();
		for(Product product : list) {
			System.out.println(product.getArticleTitle());
		}
	}
}
