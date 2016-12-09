package com.hbl.solr.util;


/**
 * JDBC数据操作工具类
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlJdbcUtil {
	
	//定义连接数据库所需要的字段  
    private static String driveClassName = "com.mysql.jdbc.Driver";  
    private  Connection conn;
    //通过配置文件为以上字段赋值  
    static{  
        try {  
            //加载驱动类  
            Class.forName(driveClassName); 
        } catch (Exception e) {  
            e.printStackTrace();
        }  
    }  
    
    public MysqlJdbcUtil(String url,String username,String password ){
    	try {
			conn = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

	public static void closeConnection(Connection conn){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 释放资源
	 * 
	 * */
	public static void closeAll(ResultSet rs, PreparedStatement ps,
			Connection conn) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (ps != null) {
				ps.close();
				ps = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void closeConn() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
}