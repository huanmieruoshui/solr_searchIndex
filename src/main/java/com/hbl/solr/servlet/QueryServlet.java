package com.hbl.solr.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.hbl.solr.search.bean.SearchParam;
import com.hbl.solr.search.bean.SearchResult;
import com.hbl.solr.search.control.SearcherController;
import com.hbl.solr.search.utils.Des3;

import net.sf.json.JSONObject;

public class QueryServlet extends HttpServlet {

	private static final long serialVersionUID = -149153040132865727L;
	public static final Logger log = Logger.getLogger(QueryServlet.class);
	private SearcherController searcherController;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setCharacterEncoding("utf-8");
		req.setCharacterEncoding("utf-8");
		long current = System.currentTimeMillis();
		PrintWriter pw = resp.getWriter();
		String param = null;
		SearchParam searchParam = null;
		SearchResult ret = null;
		searcherController = new SearcherController();
		
		if("GET".equals(req.getMethod())){
			param = req.getParameter("param");
		}else {
			param = readJsonFromRequestBody(req);
		}
		
		if(StringUtils.isBlank(param)){
			//pw.print(SearchUtils.generateResultJsontest(SearchStatus.SearchParamNullError, ret));
			return;
		}
		
		try {
			//2 解密
			param = Des3.decode(param);
			
			
			param = new String(param.getBytes());
			//3 反序列化
			searchParam = castJson2SearchParam(param);
		} catch (Exception e) {
			log.error("Failed to analyzed search parameter. param:" + param,e);
			//pw.print(SearchUtils.generateResultJson(SearchStatus.ParseSearchParamError, ret));
			return;
		}
		
		ret = searcherController.search(searchParam);
		
		long end = System.currentTimeMillis();
		long times = end -current;
		ret.setTimeConsumer(times);
		
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/json; charset=utf-8");  
		if(searchParam.getWt()=="xml"||searchParam.getWt().equals("xml")){
			pw.print(generateResultXml(ret));
		}else{
			pw.print(generateResultJson(ret));
		}
		
		
		
		log.info("The Searched complete! Time consumer:" + times);
	}


	private String generateResultXml(SearchResult ret) {
		return null;
	}

	private String generateResultJson(SearchResult ret) {
		JSONObject jsonObject = JSONObject.fromObject(ret);
		return jsonObject.toString();
	}

	@SuppressWarnings("static-access")
	private SearchParam castJson2SearchParam(String param) {
		JSONObject obj=new JSONObject().fromObject(param);
		SearchParam simInfo=(SearchParam)JSONObject.toBean(obj, SearchParam.class);
		return simInfo;
	}

	public String readJsonFromRequestBody(HttpServletRequest request){
		StringBuffer json = new StringBuffer();
		String line = null;
		
		BufferedReader br = null;
		try {
			br = request.getReader();
			while((line = br.readLine()) != null){
				json.append(line);
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to read the json of search param!",e);
		} 
		
		return json.toString();
	}
}
