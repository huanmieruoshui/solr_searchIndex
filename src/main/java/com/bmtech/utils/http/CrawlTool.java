package com.bmtech.utils.http;

import java.net.URL;

public class CrawlTool {

	static void printUsage(){
		System.out.println("usag: url-to-crawl, url must be starts with http://");
	}
	public static void main(String[]a) throws Exception{
		if(a.length == 0){
			printUsage();
			return;
		}
		if(a[0].equalsIgnoreCase("help")){
			printUsage();
			return;
		}
		if(!a[0].toLowerCase().startsWith("http://")){
			printUsage();
			return;
		}
		HttpHandler hdl = HttpHandler.getCrawlHandler();
		hdl.setReadTimeout( 60 * 60 * 1000);
		HttpCrawler clt = new HttpCrawler(new URL(a[0]));
		int code = clt.connect();
		System.out.println("get httpCode:" + code);
		System.out.println("html is:" );
		
		String html = clt.getString();
		System.out.println(html);
		
		System.out.println("http down OK!");
	}
}
