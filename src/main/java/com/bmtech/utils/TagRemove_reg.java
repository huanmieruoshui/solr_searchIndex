package com.bmtech.utils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class TagRemove_reg {
	static String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script> }
	static String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style> }
	static String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式


	static Pattern p_script= Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
	static Pattern p_style= Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
	static Pattern p_html = Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
	
	static String regEx_escape1="&[\\w|\\d]{2,5};";
	static String regEx_escape2="&[\\w|\\d]{2,5}";
	static Pattern p_escape1= Pattern.compile(regEx_escape1,Pattern.CASE_INSENSITIVE);
	static Pattern p_escape2 = Pattern.compile(regEx_escape2,Pattern.CASE_INSENSITIVE);
	static final String emp=" ";
//	public static String removeEsc(String str){
//		String htmlStr = str.replace("&lt;", "<").replace("&gt;", ">");
//		Matcher m_escape1;
//		Matcher m_escape2;
//		try {
//			m_escape1=p_escape1.matcher(htmlStr);
//			htmlStr = m_escape1.replaceAll(emp); 
//			m_escape2=p_escape2.matcher(htmlStr);
//			htmlStr = m_escape2.replaceAll(emp); 
//			return htmlStr;
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//
//		return htmlStr;//返回文本字符串
//
//	}
	public static String remove(String inputString) {
		if(inputString==null)
			return null;
		//replace centent
		String htmlStr = inputString.replace("&lt;", "<").replace("&gt;", ">");
		//含html标签的字符串
		Matcher m_style;
		Matcher m_html;
		Matcher m_script;
		Matcher m_escape1;
		Matcher m_escape2;

		try {

			m_script = p_script.matcher(htmlStr); 
			htmlStr = m_script.replaceAll( emp); //过滤script标签

			m_style = p_style.matcher(htmlStr); 
			htmlStr = m_style.replaceAll(emp); //过滤style标签

			m_html = p_html.matcher(htmlStr); 
			htmlStr = m_html.replaceAll(emp); //过滤html标签

			m_escape1=p_escape1.matcher(htmlStr);
			htmlStr = m_escape1.replaceAll(emp); //过滤escape 标签
			//			
			m_escape2=p_escape2.matcher(htmlStr);
			htmlStr = m_escape2.replaceAll(emp); //过滤escape 标签

			return htmlStr;

		}
		catch(Exception e) {
			System.err.println("yjxHtml2Text().Html2Text: " + e.getMessage());
		}

		return htmlStr;//返回文本字符串
	}   

}