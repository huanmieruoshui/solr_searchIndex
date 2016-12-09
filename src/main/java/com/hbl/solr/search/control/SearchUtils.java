package com.hbl.solr.search.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.bmtech.utils.ruledSegment.DeepMerger;
import com.bmtech.utils.ruledSegment.RuledSegment;
import com.bmtech.utils.ruledSegment.SegResult;
import com.hbl.solr.bean.Product;
import com.hbl.solr.search.CommonQuery;
import com.hbl.solr.search.bean.Constants;
import com.hbl.solr.search.bean.QueryResult;
import com.hbl.solr.search.bean.SearchParam;
import com.hbl.solr.search.bean.SearchResult;

public class SearchUtils {

	public static String buildQueryString(CommonQuery query, SearchParam param) {
		StringBuilder sb = new StringBuilder();
		String queryKeyWord = param.getQ();
		if(queryKeyWord==null||queryKeyWord.trim().length()==0||queryKeyWord.trim().equals("\"\"")){
			param.setQ("*");
			sb.append(" AND (*:*)");
		} else {
			// 特殊关系符号替换为统一关系符号
			for(String specialAnd : Constants.SPECIAL_AND_SPLIT_ARR) {
				if(param.getQ().contains(specialAnd)) {
					param.setQ(param.getQ().replace(specialAnd, Constants.AND_SPLIT)); // 将特殊and关系符号换为统一and关系符号
				}
			}
			// 关键字不为空且包含and关系符（这里的and关系符是空格）
			buildKeyString(param, Constants.AND_SPLIT, sb, Constants.AND_CONNECTOR);
			StringBuilder tempSb = new StringBuilder();
			tempSb = getQueryStr(queryKeyWord);
			String temp = tempSb.toString();
			sb.append(" AND ( ").append(temp).append(")");
		}
		if(param.getArticleIsPublished()==1) {
			sb.append(" AND ( articleIsPublished:1 )");
		} else {
			sb.append(" AND ( articleIsPublished:0 )");
		}
		return sb.toString();
	}
	
	/**
	 * 构造关键字查询字符串，目前仅支持模糊检索
	 * @param param
	 * 		SearchParam
	 * @param split
	 * 		分隔符，代表并且/或者关系
	 * @param sb
	 * 		构建的关键字查询字符串
	 * @param connector
	 * 		solr查询连接符，代表并且/或者关系
	 */
	private static void buildKeyString(SearchParam param, String split, StringBuilder sb, String connector) {
		StringBuilder tempAllSb = new StringBuilder();
		StringBuilder tempSb = new StringBuilder();
		if(param.getQ().contains(split)) { // 多词
			String[] keywords = param.getQ().split(split);
			for(String keyword : keywords) {
				if(StringUtils.isBlank(keyword)) {
					continue;
				}
				tempAllSb = getQueryStr(keyword);
				tempSb.append(tempAllSb).append(" ").append(connector).append(" ");
				tempAllSb = new StringBuilder();
			}
			tempSb = new StringBuilder(tempSb.toString().substring(0, tempSb.toString().lastIndexOf(connector)));
		} else {
			tempSb = getQueryStr(param.getQ());
		}
		String temp = tempSb.toString();
		sb.append(" AND ( ").append(temp).append(")");
	}
	
	public static StringBuilder getQueryStr(String keyword) {
		StringBuilder querySbTitle = new StringBuilder().append("(");
		StringBuilder querySbContent = new StringBuilder().append("(");
		List<String> setments = getSegment(keyword);
		for(int i=0; i<setments.size(); i++) {
			if(i>0) {
				querySbTitle.append(" AND ");
				querySbContent.append(" AND ");
			}
			querySbTitle.append("articleTitle:").append("\"").append(setments.get(i)).append("\"");
			querySbContent.append("articleContent:").append("\"").append(setments.get(i)).append("\"");
		}
		querySbTitle.append(")");
		querySbContent.append(")");
		querySbTitle.append(" OR ").append(querySbContent.toString());
		return querySbTitle;
	}

	@SuppressWarnings("rawtypes")
	public static void doSearch(CommonQuery query, SearchParam param,
			SearchResult searchResult, String queryStr) {
		String sortFiled = "";
		boolean asc = false;
		if(param.getSort()!=null&&param.getSort().length()>0){
			sortFiled = param.getSort().split(" ")[0];
			String sorts =  param.getSort().split(" ")[1];
			if(sorts=="desc"||sorts.equals("desc")){
				
			}else{
				asc = true;
			}
		}
		
		QueryResult queryResult = null;
		int begin = 0;
		if(param.getS() == 1) {
			begin = 0;
		}
		else {
			begin = param.getN() * (param.getS() - 1) ;
		}
		
		try {
			String temp ="";
			if(queryStr.trim().startsWith("AND")){
				temp = queryStr.substring(queryStr.indexOf("AND")+3);
			}
			queryResult = query.queryDataCommon(temp, begin, param.getN(), sortFiled, asc, param.getQ());
		} catch (Exception e) {
			e.printStackTrace();
		}
		searchResult.setError(queryResult.isError());
		searchResult.setRowCount(queryResult.getTotalrecord());
		int pageCount = (int) ((queryResult.getTotalrecord() - 1) / param.getN() + 1);
		searchResult.setPagesCount(pageCount);
		
		List<Product> results = new ArrayList<Product>();
		SolrDocumentList docs= (SolrDocumentList)queryResult.getResultlist();
		if(docs!=null&&docs.size()>0){
			Iterator<SolrDocument> iter = docs.iterator();
			while (iter.hasNext()) { 
				SolrDocument doc = iter.next();
				Product p= buildMapFromDoc(doc);
				results.add(p);  
			}
		}
		searchResult.setResults(results);
	}
	
	private static Product buildMapFromDoc(SolrDocument doc) {
		Product product =new Product();
		if(doc.containsKey("id")){
			product.setId(doc.getFieldValue("id").toString());
		}
		if(doc.containsKey("articleTitle")){
			product.setArticleTitle(doc.getFieldValue("articleTitle").toString());
		}
		if(doc.containsKey("articleTags")){
			product.setArticleTags(doc.getFieldValue("articleTags").toString());
		}
		if(doc.containsKey("articleContent")){
			product.setArticleContent(doc.getFieldValue("articleContent").toString());
		}
		if(doc.containsKey("articlePermalink")){
			product.setArticlePermalink(doc.getFieldValue("articlePermalink").toString());
		}
		if(doc.containsKey("articleCreateDate")){
			product.setArticleCreateDate(doc.getFieldValue("articleCreateDate").toString());
		}
		if(doc.containsKey("articleUpdateDate")){
			product.setArticleUpdateDate(doc.getFieldValue("articleUpdateDate").toString());
		}
		return product;
	}
	
	public static List<String> getSegment(String keyword) {
		RuledSegment rs = new RuledSegment(keyword);
		List<String> list = new ArrayList<String>();
	    Iterator<SegResult> itr = rs.segment(new DeepMerger());
	    while (itr.hasNext()) {
	    	SegResult res = itr.next();
	    	if (!res.isSymbol) {
	    		//System.out.println(res.strValue);
	    		list.add(res.strValue);
	    	}
	    }
	    return list;
	}
	
}
