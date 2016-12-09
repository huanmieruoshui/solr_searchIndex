package com.bmtech.utils.rds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;
import com.bmtech.utils.rds.SourceDefine.DataSourceDefine;
import com.bmtech.utils.rds.SourceDefine.Sqls;

public class RDS {
	private PreparedStatement pps;
	private Connection conn;
	private final boolean shared;
	private String source;
	private String sql;
	private RDS(String source){
		this(source, null);
	}

	private RDS(String source, Connection conn){
		this.source = source;
		this.conn = conn;
		if(this.conn == null) {
			shared = false;
			BmtLogger.instance().log(LogLevel.Debug,
					"make private connection for rds %s", 
					source);
		}else {
			shared = true;
//			BmtLogger.instance().log(LogLevel.Debug,
//					"make shared connection for rds %s", 
//					source);
		}
	}
	/**
	 * 
	 * @return before not access and using private conntion, return null;
	 * after accessing, return the connection this instance used
	 */
	public Connection getMyConnection() {
		return this.conn;
	}
	/**
	 * touch the data source, connect to it and make a prepared statment
	 * @throws NotDefineException
	 * @throws SQLException
	 */
	private void access(String sql, boolean largeResultSetSupport) throws SQLException{
		if(sql == null)
			throw new SQLException("sql '"+sql+"' is not defined");
		this.sql = sql;
		//获取配置文件db_def.rds中的var,src
		DataSourceDefine def = SourceDefine.instance().getDataSourceDefine(source);
		if(conn == null) {
			if(def == null)
				throw new SQLException("sourceDefine '"+source+"' is not defined, besure defined at SRC block and detailed descript");
			conn = def.getNewConnection();
		}
//		else {
//			BmtLogger.instance().log(LogLevel.Debug, "shared connection not need make new one");
//		}

		if(largeResultSetSupport){
			pps = conn.prepareStatement(sql,java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			pps.setFetchSize(Integer.MIN_VALUE);
			//((com.mysql.jdbc.Statement)pps).enableStreamingResults(); 	
		}else{
			pps = conn.prepareStatement(sql);
		}
	}

	public static boolean connectionValid(RDS rds) throws SQLException{
		if(rds == null){
			return false;
		}
		return rds.connectionValid();
	}
	public static void close(RDS rds){
		if(rds != null){
			rds.close();
		}
	}
	public boolean connectionValid() throws SQLException{
		if(this.conn == null || this.conn.isClosed()){
			return false;
		}
		if(this.pps == null){
			return false;
		}
		return true;
	}
	public void setAutoCommit(boolean auto) throws SQLException{
		this.conn.setAutoCommit(auto);
	}
	public void commit() throws SQLException{
		if(conn != null){
			if(!conn.getAutoCommit()){
				conn.commit();
			}
		}
	}

	/**
	 * close connection and other database source.
	 * <br>iif the connection is shared, this operation will not close 
	 * the connection, 
	 */
	public void close(){
		if(pps != null){
			try{
				pps.close();
			}catch(Exception e){
				try {
					pps.close();
				} catch (Exception e1) {
					BmtLogger.instance().log(e1, "got exception when colse pps", e);
				}
			}
			pps = null;
		}
		if(!shared) {
			if(conn != null){
				BmtLogger.instance().log(LogLevel.Debug, "close connection");
				try{
					if(!conn.isClosed()){
						conn.close();
					}
				}catch(Exception e){
					try {
						conn.close();
					} catch (SQLException e1) {
						BmtLogger.instance().log(e1, "got exception when colse conn", e);
					}
				}
				conn = null;
			}
		}
	}
	/**
	 * @see #load()
	 * @return
	 * @throws SQLException
	 */
	public ResultSet executeQuery() throws SQLException{
		return pps.executeQuery();
	}
	/**
	 * load data from db
	 * @return
	 * @throws SQLException
	 */
	public ResultSet load() throws SQLException{
		return pps.executeQuery();
	}
	/**
	 * 
	 * @return true if is a select action
	 * @throws SQLException
	 */
	public boolean execute() throws SQLException{
		return pps.execute();
	}

	public void setInt(int idx,int value) throws SQLException{
		if(pps == null )
			throw new NullPointerException("pps is null");
		pps.setInt(idx, value);
	}
	public void setLong(int idx,long value) throws SQLException{
		if(pps == null )
			throw new NullPointerException("pps is null");
		pps.setLong(idx, value);
	}
	public void setBlob(int idx, byte[] value) throws SQLException {
		if(pps == null )
			throw new NullPointerException("pps is null");
		if (value == null)
			this.pps.setBinaryStream(idx, null);
		else
			this.pps.setBinaryStream(idx, new ByteArrayInputStream(value));
	}
	public void setBlob(int idx, InputStream value) throws SQLException {
		if(pps == null )
			throw new NullPointerException("pps is null");
		pps.setBinaryStream(idx, value);
	}
	public void setString(int idx, String value) throws SQLException{
		if(pps == null )
			throw new NullPointerException("pps is null");
		pps.setString(idx, value);
	}
	public void setDouble(int idx, double value) throws SQLException{
		if(pps == null )
			throw new NullPointerException("pps is null");
		pps.setDouble(idx, value);
	}
	public void finalize(){
		this.close();
	}

	public static byte[] getBlob(ResultSet rs, int idx) throws SQLException, IOException {
		ByteArrayOutputStream bops = new ByteArrayOutputStream();
		InputStream ips = rs.getBinaryStream(idx);
		byte[]buf = new byte[4096];
		while(true) {
			int readed = ips.read(buf);
			if(readed == -1) {
				break;
			}
			bops.write(buf, 0, readed);
		}
		ips.close();
		return bops.toByteArray();
	}
	public String toString() {
		return this.source + "--" + this.pps;
	}
	/**
	 * using private connection and no large data support
	 * @param key
	 * @return
	 * @throws NotDefineException
	 * @throws SQLException
	 */
	public static RDS getRDSByKey(
			String key) throws SQLException{
		return getRDSByKey(key, false);
	}
	/**
	 * using private connection to make a RDS
	 * @param key
	 * @param largeResultSetSupport
	 * @return
	 * @throws NotDefineException
	 * @throws SQLException
	 */
	public static RDS getRDSByKey(String key,
			boolean largeResultSetSupport) throws  SQLException{
		return getRDSByKey(key, largeResultSetSupport, null);
	}

	public static RDS getRDSByKey(String key, Connection conn) throws SQLException{
		return getRDSByKey(key, false, conn);
	}
	/**
	 * using shared(if conn is not null) connection, construct a RDS
	 * @param key
	 * @param largeResultSetSupport
	 * @param conn
	 * @return
	 * @throws NotDefineException
	 * @throws SQLException
	 */
	public static RDS getRDSByKey(String key,
			boolean largeResultSetSupport, Connection conn) throws SQLException{
		String sql = Sqls.instance().getSql(key);
		if(sql == null){
			throw new SQLException("sql "+key+" is not defined");
		}
		SqlSplit spl = new SqlSplit(sql);
		return spl.getAndAccessRDS(largeResultSetSupport, conn);
	}

	/**
	 * using private connection and no large data support
	 * @param key
	 * @return
	 * @throws NotDefineException
	 * @throws SQLException
	 */
	public static RDS getRDSByDefine(String source,
			String sql) throws SQLException{
		return getRDSByDefine(source, sql, false);
	}
	/**
	 * using private connection to make a RDS
	 * @param key
	 * @param largeResultSetSupport
	 * @return
	 * @throws NotDefineException
	 * @throws SQLException
	 */
	public static RDS getRDSByDefine(String source, String sql,
			boolean largeResultSetSupport) throws SQLException{
		return getRDSByDefine(source, sql, largeResultSetSupport, null);
	}

	public static RDS getRDSByDefine(String source, String sql, Connection conn) throws SQLException{
		return getRDSByDefine(source, sql, false, conn);
	}
	/**
	 * using shared(if conn is not null) connection, construct a RDS
	 * @param key
	 * @param largeResultSetSupport
	 * @param conn
	 * @return
	 * @throws NotDefineException
	 * @throws SQLException
	 */
	public static RDS getRDSByDefine(String source, String sql,
			boolean largeResultSetSupport, Connection conn) throws SQLException{
		SqlSplit spl = new SqlSplit(source, sql);
		return spl.getAndAccessRDS(largeResultSetSupport, conn);
	}



	public String getSql() {
		return sql;
	}

	static class SqlSplit{
		private final String sql;
		private final String source;
		public SqlSplit(String sql) throws SQLException{
			if(sql == null)
				throw new SQLException(sql+" is NULL");
			String str[] = sql.split("::");
			if(str.length != 2){
				throw new SQLException("'" + sql +"' is not splitable or format error");
			}
			source = str[0];
			this.sql = str[1];
		}
		public SqlSplit(String src, String sql) throws SQLException{
			if(src == null)
				throw new SQLException("src is NULL");
			if(sql == null)
				throw new SQLException("sql is NULL");
			source = src;
			this.sql = sql;
		}
		/**
		 * support large resultSet; Only for Query
		 * @param largeResultSetSupport
		 * @return
		 * @throws NotDefineException
		 * @throws SQLException
		 */
		RDS getAndAccessRDS(boolean largeResultSetSupport, Connection conn) throws SQLException{
			RDS rds = new RDS(source, conn);
			rds.access(sql, largeResultSetSupport);
			return rds;
		}
	}

	/**
	 * using private connection and no large data support
	 * @param key
	 * @return
	 * @throws NotDefineException
	 * @throws SQLException
	 */
	public static RDS getRDSByDefine(String sql) throws SQLException{
		return getRDSByDefine( sql, false);
	}
	/**
	 * using private connection to make a RDS
	 * @param key
	 * @param largeResultSetSupport
	 * @return
	 * @throws NotDefineException
	 * @throws SQLException
	 */
	public static RDS getRDSByDefine( String sql,
			boolean largeResultSetSupport) throws SQLException{
		return getRDSByDefine( sql, largeResultSetSupport, null);
	}
	public static RDS getRDSByDefine( String sql, Connection conn) throws SQLException{
		return getRDSByDefine( sql, false, conn);
	}
	/**
	 * using shared(if conn is not null) connection, construct a RDS
	 * @param key
	 * @param largeResultSetSupport
	 * @param conn
	 * @return
	 * @throws NotDefineException
	 * @throws SQLException
	 */
	public static RDS getRDSByDefine( String sql,
			boolean largeResultSetSupport, Connection conn) throws SQLException{
		SqlSplit spl = new SqlSplit( sql);
		return spl.getAndAccessRDS(largeResultSetSupport, conn);
	}
	
}