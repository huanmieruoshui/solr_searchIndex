package com.hbl.solr.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.hbl.solr.config.Config;
import com.hbl.solr.process.task.LogTask;
import com.hbl.solr.process.task.ServerTask;

public class ContextListener implements ServletContextListener {

	public static final Logger log = Logger.getLogger(ContextListener.class);

	public void contextDestroyed(ServletContextEvent event) {
		timer.cancel();  
        event.getServletContext().log("销毁定时器");  
	}
	
	private java.util.Timer timer = null;  

	public void contextInitialized(ServletContextEvent event) {
		
		log.info("==================contextInitialized===================");
        timer = new java.util.Timer(true);  
        event.getServletContext().log("启动");  
  
		
		//索引定时更新
		timer.schedule(new LogTask(),0, 1000);  
	    timer = new java.util.Timer(true);  
       
        
        //服务器异常检测
        timer.schedule(new ServerTask(),0, Integer.parseInt(Config.getString("servertask")));  
        timer = new java.util.Timer(true);  
        
        event.getServletContext().log("contextlistener over................");  
	}

}
