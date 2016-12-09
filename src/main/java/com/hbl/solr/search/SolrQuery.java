package com.hbl.solr.search;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.hbl.solr.bean.Product;
import com.hbl.solr.search.bean.SearchResult;
import com.hbl.solr.search.utils.Des3;
import com.hbl.solr.util.SolrUtil;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
* @className:Query.java
* @classDescription:
* @author:hbl
* @createTime:2016年6月28日
*/
public class SolrQuery {

	@SuppressWarnings("rawtypes")
	public SearchResult getSolrQuery(String param, String host) {
		CloseableHttpClient client = SolrUtil.getHttpClient();
		SearchResult result = new SearchResult();
		try {
			//参数加密
			//param = new String(param.getBytes(),"GBK");
			System.out.println(param);
			param = Des3.encode(param);
			StringEntity entity = new StringEntity(param);
			HttpPost request = new HttpPost(host);
			request.addHeader(HttpHeaders.ACCEPT,"application/json");
	        request.setEntity(entity);
	        HttpResponse response = client.execute(request);
	        String bodyAsString = EntityUtils.toString(response.getEntity()); 
	        //System.out.println(bodyAsString);
	        if(StringUtils.isNotBlank(bodyAsString)){ // 判断是否为null或者"",去空格
	        	JSONObject obj = JSONObject.fromObject(bodyAsString);
	        	JsonConfig jsonConfig = new JsonConfig();
	    		jsonConfig.setRootClass(SearchResult.class);
	    		Map<String, Class> classMap = new HashMap<String, Class>();
	    		classMap.put("results", Product.class); // 指定JsonRpcRequest的request字段的内部类型
	    		jsonConfig.setClassMap(classMap);
	    		result =( SearchResult)JSONObject.toBean(obj, jsonConfig);
	    		System.out.println("size:"+result.getRowCount()+"  耗时："+result.getTimeConsumer());
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
