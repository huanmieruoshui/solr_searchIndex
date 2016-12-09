package com.hbl.solr.process.task;

import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.hbl.solr.config.Config;
import com.hbl.solr.mail.MainAlarm;
import com.hbl.solr.util.HttpTookitEnhance;

public class ServerTask extends TimerTask{
	
	/**发生异常时候接收邮件地址**/
	private String[] maillist;
	private String[] solrservers;
	private MainAlarm mailAlarm;
	
	public ServerTask(){
		maillist = Config.getString("maillist").split(",");
		solrservers = Config.getString("solr_watch_shards").split(",");
		mailAlarm = new MainAlarm(maillist);
	}

	@Override
	public void run() {
		if(solrservers==null)
			return ;
		
		for (int i = 0; i < solrservers.length; i++) {
			try {
				String url = solrservers[i];
				String content=HttpTookitEnhance.doGet(url, null, "UTF-8", true);
				JSONObject jo = new JSONObject(content);
				JSONObject johead = jo.getJSONObject("responseHeader");
				int status = johead.getInt("status");
				if(status!=0){
					//send mail
					JSONObject joerror = jo.getJSONObject("error");
					mailAlarm.sendStack(null, "solr监控-->"+joerror.getString("msg"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
