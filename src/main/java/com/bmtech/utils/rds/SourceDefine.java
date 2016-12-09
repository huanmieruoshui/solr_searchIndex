package com.bmtech.utils.rds;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.bmtech.utils.KeyValuePair;
import com.bmtech.utils.io.ConfigReader;
import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;

public class SourceDefine {
	public static class Sqls {
		private static Sqls instance = new Sqls();
		private Map<String,String>sqls = new HashMap<String, String>();
		private Sqls(){
			init();
		}
		private void init(){
			File file = new File(SourceDefine.RDS_CONFIG, "sqls.rdf");
			ConfigReader cr = new ConfigReader(file,"main");
			sqls = cr.read2Map();
		}
		/**
		 * case sensitive
		 * @param key
		 * @return
		 */
		public String getSql(String key){
			return sqls.get(key);
		}
		public static final Sqls instance(){
			return instance;
		}
	}
	public static final File RDS_CONFIG = new File("./config/rds/");

	public class DataSourceDefine{
		String user, pwd, host, port, db, url;//the url to connect to the db source
		String driver;
		private DataSourceDefine(){}
		private void compile() throws NullPointerException{
			if(url == null)
				throw new NullPointerException(" url is not set");
			this.url = url.replace("$database$", db).replace("$host$", host).replace("$port$", port);
			try{
				Class.forName(driver);
			} catch (ClassNotFoundException e){
				BmtLogger.instance().log(e, "got exception forname() ");
			}
		}
		/**
		 * get a connection of this data source
		 * @return
		 * @throws SQLException
		 */
		public Connection getNewConnection() throws SQLException{
			return DriverManager.getConnection(url, user, pwd);
		}

		public String toString(){
			return user+':'+pwd+'~'+url;
		}
	}
	private static final SourceDefine instance = new SourceDefine();
	private HashMap<String,String>vars = new HashMap<String,String>();
	private HashMap<String,DataSourceDefine> res = new HashMap<String,DataSourceDefine>();

	private static final String sourceDefine = "db_def.rds";
	public static final SourceDefine instance(){
		return instance;
	}
	private SourceDefine(){
		
		ConfigReader varDef = new ConfigReader(new File(RDS_CONFIG, sourceDefine), "var");
		ConfigReader srcDef = new ConfigReader(new File(RDS_CONFIG, sourceDefine), "src");

		Map<String, String> varMap = varDef.read2Map();
		Iterator<Entry<String, String>> varItr = varMap.entrySet().iterator();
		while(varItr.hasNext()){
			Entry<String, String> e = varItr.next();
			vars.put(e.getKey(), e.getValue());
		}

		ArrayList<KeyValuePair<String, String>> srcMap = srcDef.getAllConfig();
		for(KeyValuePair<String, String> pair : srcMap){
			if(pair.getKeyString().compareTo("SRC") == 0){
				String key = pair.value;
				DataSourceDefine def = getDSDefine(key);
				res.put(key, def);
			}
		}
	}
	private String getValue(String key){
		if(key == null)
			return null;
		String str = vars.get(key);
		if(str == null)
			return vars.get(key.toLowerCase());
		return str;
	}
	/**
	 * get source define using source's name.
	 * <br>we expect that :
	 * <br>define global source defination
	 * <br>evoke it when use, not to define in ever
	 * @param key
	 * @return
	 */
	public DataSourceDefine getDataSourceDefine(String key){
		if(key == null)
			return null;
		DataSourceDefine def = res.get(key);
		if(def == null)
			return res.get(key.toLowerCase());
		return def;
	}
	private String getConfigedValue(String key,ConfigReader cr){
		String value = cr.getValue(key);
		if(value == null)//not set, use global define
			return getValue(key);
		if(value.length() == 0)//empty string
			return value;
		if(value.charAt(0)==('$')&&value.charAt(value.length()-1)=='$'){
			//use global Defined value
			value=value.substring(1, value.length()-1);
			return getValue(value);
		}else
			return value;
	}
	private DataSourceDefine getDSDefine(String name){
		ConfigReader cr = new ConfigReader(new File(RDS_CONFIG, sourceDefine), name);

		DataSourceDefine def = new DataSourceDefine();

		def.host   = getConfigedValue("host", cr);
		def.port   = getConfigedValue("port", cr);
		def.user   = getConfigedValue("user", cr);
		def.pwd    = getConfigedValue("password", cr);
		def.db     = getConfigedValue("database", cr);
		def.url    = getConfigedValue("url", cr);
		def.driver = getConfigedValue("driver", cr);
		if(def.host == null){
			BmtLogger.instance().log(LogLevel.Warning, name + " null value def.host" );
		}

		if(def.port == null){
			BmtLogger.instance().log(LogLevel.Warning, name + "null value def.port" );
		}

		if(def.user == null){
			BmtLogger.instance().log(LogLevel.Warning, name + "null value def.user" );
		}

		if(def.pwd  == null){
			BmtLogger.instance().log(LogLevel.Warning, name + "null value def.pwd " );
		}

		if(def.db == null){
			BmtLogger.instance().log(LogLevel.Warning, name + "null value v" );
		}

		if(def.url == null){
			BmtLogger.instance().log(LogLevel.Warning, name + "null value def.url" );
		}

		if(def.driver == null){
			BmtLogger.instance().log(LogLevel.Warning, name + "null value def.driver" );
		}
		def.compile();
		return def;
	}
	
}
