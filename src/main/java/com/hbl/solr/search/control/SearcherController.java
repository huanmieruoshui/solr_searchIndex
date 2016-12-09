package com.hbl.solr.search.control;

import org.apache.log4j.Logger;

import com.hbl.solr.search.CommonQuery;
import com.hbl.solr.search.bean.SearchParam;
import com.hbl.solr.search.bean.SearchResult;

public class SearcherController {
	
	public static final Logger log = Logger.getLogger(SearcherController.class);
	private UserSearchedHandler handler = new UserSearchedHandler();

	public SearchResult search(SearchParam param) {
		if(!paramValidate(param)){
			return null;
		}
		SearchResult result = new SearchResult();
		handler.handle(param);
		
		log.info("Attempt to search product! ");
		
		CommonQuery searcher = null;
		try {
			searcher = new CommonQuery("solr_blog");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String queryStr = SearchUtils.buildQueryString(searcher, param);
		SearchUtils.doSearch(searcher, param, result, queryStr);
		
		return result;
	}

	private boolean paramValidate(SearchParam param) {
		return true;
	}

}
