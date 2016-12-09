package com.hbl.solr.search.control;

import org.apache.log4j.Logger;

import com.hbl.solr.search.bean.SearchParam;

/**
 * 记录日志
 * @author Administrator
 *
 */
public class UserSearchedHandler {

	public static final Logger log =Logger.getLogger(UserSearchedHandler.class);
	
	public void handle(SearchParam param){
		//LogUtils.write(param.toString(),Config.getString("log_path")+".log");
		
	}
}
