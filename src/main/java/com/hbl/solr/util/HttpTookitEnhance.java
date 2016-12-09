package com.hbl.solr.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;

public class HttpTookitEnhance {
	
	private static HttpClient client;

	private static HttpMethod method;
	
	public static String doGet(String url, String queryString, String charset){
		try {
			client = new HttpClient();
			client.getHostConfiguration().setHost("baidu.com", 80, "http");
			method = new GetMethod(url);
			method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
			HttpMethodParams params =new HttpMethodParams();
			params.setParameter("Server", "nginx");
			params.setParameter("Content-Type", "text/html; charset=utf-8");
			params.setParameter("Content-Encoding", "gzip");
			params.setParameter("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt");
			method.setParams(params);
			method.setRequestHeader("Cookie", "special_cookie=value");
			client.executeMethod(method);
			String temp = method.getResponseBodyAsString();
			return temp;
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			method.releaseConnection();
		}
		return null;
	}
	
	
	public synchronized static String doGet(String url,  String charset){
		try {
			client = new HttpClient();
			method = new GetMethod(url);
			client.setConnectionTimeout(100000);
			method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,charset);  
			client.executeMethod(method);
			String temp = method.getResponseBodyAsString();
			return temp;
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			method.releaseConnection();
		}
		return null;
	}
	
	public static String doGet(String url, String queryString, String charset,
			boolean pretty) {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		client.setConnectionTimeout(10000);
		client.setTimeout(10000);
		try {
			if (queryString != null && !queryString.equals(""))
				method.setQueryString(URIUtil.encodeQuery(queryString));
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty)
						response.append(line).append(
								System.getProperty("line.separator"));
					else
						response.append(line);
				}
				reader.close();
			}
		} catch (URIException e) {
		} catch (IOException e) {
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}

	public static String doPost(String url, Map<String, String> params,
			String charset, boolean pretty) {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		HttpMethod method = new PostMethod(url);
		if (params != null) {
			HttpMethodParams p = new HttpMethodParams();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				p.setParameter(entry.getKey(), entry.getValue());
			}
			method.setParams(p);
		}
		try {
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty)
						response.append(line).append(
								System.getProperty("line.separator"));
					else
						response.append(line);
				}
				reader.close();
			}
		} catch (IOException e) {
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}

}
