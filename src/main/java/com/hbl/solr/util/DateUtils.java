package com.hbl.solr.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

	public static String converDate(Date date){
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		String item= sdf2.format(date);
		System.out.println(item);
		return item;
	}
	
	
	public static String converDate(String date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		try {
			Date d=sdf.parse(date);
			return sdf2.format(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getLastYear(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date=new Date();//取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR,-1);//把日期往后增加一天.整数往后推,负数往前移动
		calendar.add(Calendar.DATE,1);
		date=calendar.getTime(); //这个时间就是日期往后推一天的结果 
		return sdf.format(date);
	}
	
	public static String getLastMonth(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date=new Date();//取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH,-1);//把日期往后增加一天.整数往后推,负数往前移动
		date=calendar.getTime(); //这个时间就是日期往后推一天的结果 
		return sdf.format(date);
	}
	
	public static String getRelaDay(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date=new Date();//取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE,-7);//把日期往后增加一天.整数往后推,负数往前移动
		date=calendar.getTime(); //这个时间就是日期往后推一天的结果 
		return sdf.format(date);
	}
	
	public static String getCurrentYear(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}
}
