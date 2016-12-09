package com.hbl.solr.mail;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hbl.solr.config.Config;

/**
 * 邮件发送
 * @author Administrator
 *
 */
public class MainAlarm {
	

	private static String[] EmailRecipient ;
	
	@SuppressWarnings("static-access")
	public MainAlarm(String[] emails){
		this.EmailRecipient=emails;
	}
	
	public  void sendWarn(String content){
		String title ="提示信息";
		doAlarm(title,content);
	}
	
	/**
	 * 发送错误信息
	 * @param content
	 */
	public  void sendError(String content){
		String title ="错误信息";
		doAlarm(title,content);
	}
	
	public  void sendStack(Exception ex,String message){
		if(ex!=null){
			ex.printStackTrace();
			
			// 报警
			StringWriter sw = new StringWriter();
	        PrintWriter pw = new PrintWriter(sw);
	        ex.printStackTrace(pw);
			
			String alarmtitle = "服务器出现问题：" +message;
			doAlarm(alarmtitle, sw.toString());
		}else{
			String alarmtitle = "服务器出现问题：" +message;
			doAlarm(alarmtitle, message);
		}
		
		
	}
	
	/**
	 * 执行报警
	 */
	private  void doAlarm(String alarmtitle, String alarmcontent){
		
		alarmcontent =alarmcontent+"\n\n";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		alarmcontent +="预警生成日期：";
		alarmcontent +=sdf.format(new Date());
		sendMailByAsynchronousMode(EmailRecipient, alarmtitle, alarmcontent, false);
	}
	
	private  void sendMailByAsynchronousMode(String[] emailRecipient2,
			String alarmtitle, String alarmcontent, boolean b) {
	         try {
				//这个类主要是设置邮件
				  MailSenderInfo mailInfo = new MailSenderInfo(); 
				  mailInfo.setMailServerHost(Config.getString("mailserverhost")); 
				  mailInfo.setMailServerPort(Config.getString("mailserverport")); 
				  mailInfo.setValidate(true); 
				  mailInfo.setUserName(Config.getString("username")); 
				  mailInfo.setPassword(Config.getString("password"));//您的邮箱密码 
				  mailInfo.setFromAddress(Config.getString("username")); 
				  
				  for(int i=0;i<emailRecipient2.length;i++){
					  
					  mailInfo.setToAddress(emailRecipient2[i]); 
					  mailInfo.setSubject(alarmtitle); 
					  mailInfo.setContent(alarmcontent); 
					  SimpleMailSender sms = new SimpleMailSender();
				      sms.sendTextMail(mailInfo);//发送文体格式 
				  }
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
