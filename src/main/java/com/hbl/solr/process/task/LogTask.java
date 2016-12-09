package com.hbl.solr.process.task;

import java.util.TimerTask;

public class LogTask  extends TimerTask {
	

	
	@Override
	public void run() {
		/*System.out.println("执行了索引更新");
		try {
			CommonProcess process=new CommonProcess(new MysqlJdbcUtil(Config.getString("zh_CN_url"),Config.getString("zh_CN_username"),Config.getString("zh_CN_password")));
			process.processIndex();
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.write(e.getMessage(), Config.getString("long_err"));
		}*/
		
	}

}
