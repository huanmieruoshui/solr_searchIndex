package com.bmtech.utils.http;

import java.sql.ResultSet;

import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;
import com.bmtech.utils.rds.RDS;

public class DBCookieHelper extends CookieHelper{
	public static abstract class HandlerTransor{
		public abstract void setHandler(HttpHandler hdl);
	}
	final String dataSource;
	final int waitRoundSeconds;
	public DBCookieHelper(String cookieId, String dataSource) {
		super(cookieId);
		this.dataSource = dataSource;
		this.waitRoundSeconds = cr.getInt("waitRoundSeconds", 10);
	}
	public HttpHandler blockUntilLoginAndStartHandlerRefresher(final HandlerTransor transor){
		final CookieInfos cr = blockUntilLogin();
		new Thread(){

			public void run(){
				CookieInfos nowHandler = cr;
				while(true){
					try {
						sleep(10 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					CookieInfos newHandler = blockUntilLogin();
					if(newHandler.cookieSetTime == cr.cookieSetTime){
						continue;
					}else{
						nowHandler = newHandler;
						transor.setHandler(nowHandler.handler);
					}
				}
			}

		}.start();
		return cr.handler;
	}
	public CookieInfos blockUntilLogin(){
		
		while(true){
			try {
				CookieInfos cInfo = this.loadCookieInfo();
				if(cInfo != null){
					return cInfo;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(waitRoundSeconds* 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public CookieInfos loadCookieInfo() throws Exception {
		RDS rds = null;
		try{
			rds = RDS.getRDSByDefine(dataSource, "select intime,userAgent,cookie1,cookie2, now() from cookiecopy where workid = ? order by intime desc limit 1");
			rds.setString(1, this.cookieId);
			ResultSet rs = rds.load();
			
			if(rs.next()){
				long intime = rs.getTimestamp(1).getTime();
				String userAgent = rs.getString(2);
				String cookie1 = rs.getString(3);
				String cookie2 = rs.getString(4);
				long now = rs.getTimestamp(5).getTime();
				long t = (now - intime)/60 / 1000;
				if(t < super.cookieExpireMinutes  ){
					BmtLogger.instance().log(LogLevel.Debug,
							"get GOOD cookieset for %s", super.cookieId);
					CookieInfos cinfos = new CookieInfos(intime, userAgent, cookie1, cookie2);
					return cinfos;
				}else{
					BmtLogger.instance().log(LogLevel.Debug,
							"get INVALID cookieset for %s", super.cookieId);
					return null;
				}

			}else{
				BmtLogger.instance().log(LogLevel.Debug,
						"get NO cookieset for %s", super.cookieId);
				return null;
			}
		}finally{
			if(rds != null){
				rds.close();
			}
		}
	}

}
