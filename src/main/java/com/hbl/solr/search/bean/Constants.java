package com.hbl.solr.search.bean;
/**
* @className:Constants.java
* @classDescription:
* @author:hbl
* @createTime:2016年12月5日
*/
public class Constants {

	// 【并且】相关对象
	public final static String AND_SPLIT = " "; // 统一and关系符号，半角空格
	public final static String[] SPECIAL_AND_SPLIT_ARR = {"　"}; // 特殊and关系符号数组，目前包括全角空格。此处定义成数组是为了以后再有特殊表示and关系的符号，可以直接加入到数据中
	public final static String AND_CONNECTOR = "AND";
	// 【或者】相关对象
	public final static String OR_SPLIT = "$@$";
	public final static String OR_CONNECTOR = "OR";
}
