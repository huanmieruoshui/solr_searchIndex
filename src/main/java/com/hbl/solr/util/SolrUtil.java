package com.hbl.solr.util;

import java.io.IOException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class SolrUtil {

	public static CloseableHttpClient getHttpClient() {
		return client;
	}
	
	private static CloseableHttpClient client;
	
	static {
		try {
			client = HttpClientBuilder.create().build();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					System.out.println("关闭HttpClient");
					try {
						client.close();
					} catch(IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
