package com.bmtech.utils.rds;

import java.sql.Connection;
import java.sql.SQLException;

import com.bmtech.utils.rds.RDS.SqlSplit;

public class RDSHost {
	public final String sourceDefine;
	public RDSHost(String sourceDefine){
		this.sourceDefine = sourceDefine;
	}
	
	public RDS sql(String sql) throws SQLException{
		return sql(sql, false, null);
	}
	public RDS sql(String sql, boolean largeResultSetSupport) throws SQLException{
		return sql(sql, largeResultSetSupport, null);
	}

	public RDS sql(String sql, Connection conn) throws SQLException{
		return sql(sql, false, conn);
	}
	public RDS sql(String sql,
			boolean largeResultSetSupport, Connection conn) throws SQLException{
		SqlSplit spl = new SqlSplit(sourceDefine, sql);
		return spl.getAndAccessRDS(largeResultSetSupport, conn);
	}
	
}
