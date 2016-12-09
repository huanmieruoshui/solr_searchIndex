package com.bmtech.utils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class TagRemove_reg {
	static String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; //����script��������ʽ{��<script[^>]*?>[\\s\\S]*?<\\/script> }
	static String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; //����style��������ʽ{��<style[^>]*?>[\\s\\S]*?<\\/style> }
	static String regEx_html = "<[^>]+>"; //����HTML��ǩ��������ʽ


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
//		return htmlStr;//�����ı��ַ���
//
//	}
	public static String remove(String inputString) {
		if(inputString==null)
			return null;
		//replace centent
		String htmlStr = inputString.replace("&lt;", "<").replace("&gt;", ">");
		//��html��ǩ���ַ���
		Matcher m_style;
		Matcher m_html;
		Matcher m_script;
		Matcher m_escape1;
		Matcher m_escape2;

		try {

			m_script = p_script.matcher(htmlStr); 
			htmlStr = m_script.replaceAll( emp); //����script��ǩ

			m_style = p_style.matcher(htmlStr); 
			htmlStr = m_style.replaceAll(emp); //����style��ǩ

			m_html = p_html.matcher(htmlStr); 
			htmlStr = m_html.replaceAll(emp); //����html��ǩ

			m_escape1=p_escape1.matcher(htmlStr);
			htmlStr = m_escape1.replaceAll(emp); //����escape ��ǩ
			//			
			m_escape2=p_escape2.matcher(htmlStr);
			htmlStr = m_escape2.replaceAll(emp); //����escape ��ǩ

			return htmlStr;

		}
		catch(Exception e) {
			System.err.println("yjxHtml2Text().Html2Text: " + e.getMessage());
		}

		return htmlStr;//�����ı��ַ���
	}   

}