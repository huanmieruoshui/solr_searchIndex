package com.hbl.solr.util;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
* @className:SolrUtil.java
* @classDescription:
* @author:hbl
* @createTime:2016年6月28日
*/
public class SolrUtil {
	
	private static CloseableHttpClient client;
	private static PoolingHttpClientConnectionManager cm = null; 	
	private final static Object syncLock = new Object();
	
	static {
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .build();
        cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);
        Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("关闭HttpClient连接池");
				try {
					if(cm!=null) {
						cm.shutdown();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static CloseableHttpClient getHttpClient() {
		if(client == null){
            synchronized (syncLock){
                if(client == null){
                	System.out.println("新建client连接...");
                	client = HttpClients.custom()
                		.setConnectionManager(cm)
                		.build();
                	System.out.println("新建client连接成功!");
                }
            }
        }
        return client;
	}
}
