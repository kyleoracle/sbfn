package com.oracle.objstorage.db;

import java.util.HashMap;
import java.util.Map;

public class DBUtil {
	private static final Map<String,String> RESERVED_MAP = new HashMap<String,String>(){
		{ 
			put("DROP","Y");
			put("DECIMAL","Y");
			put("PRIOR","Y");
			put("FROM","Y");
			put("PUBLIC","Y");
			put("FOR","Y");
			put("UNION","Y");
			put("FLOAT","Y");
			put("START","Y");
			put("RAW","Y");
			put("VALUES","Y");
			put("AS","Y");
			put("GRANT","Y");
			put("TABLE","Y");
			put("ASC","Y");
			put("HAVING","Y");
			put("ALTER","Y");
			put("WITH","Y");
			put("CONNECT","Y");
			put("GROUP","Y");
			put("OF","Y");
			put("SYNONYM","Y");
			put("VARCHAR","Y");
			put("DISTINCT","Y");
			put("UNIQUE","Y");
			put("BY","Y");
			put("INTEGER","Y");
			put("LONG","Y");
			put("ORDER","Y");
			put("MINUS","Y");
			put("NULL","Y");
			put("REVOKE","Y");
			put("SELECT","Y");
			put("OR","Y");
			put("AND","Y");
			put("LOCK","Y");
			put("ANY","Y");
			put("ON","Y");
			put("BETWEEN","Y");
			put("LIKE","Y");
			put("DEFAULT","Y");
			put("CHAR","Y");
			put("TO","Y");
			put("COMPRESS","Y");
			put("MODE","Y");
			put("RESOURCE","Y");
			put("ELSE","Y");
			put("UPDATE","Y");
			put("IN","Y");
			put("DESC","Y");
			put("TRIGGER","Y");
			put("OPTION","Y");
			put("SMALLINT","Y");
			put("PCTFREE","Y");
			put("SHARE","Y");
			put("VIEW","Y");
			put("INTO","Y");
			put("INSERT","Y");
			put("CREATE","Y");
			put("CHECK","Y");
			put("EXISTS","Y");
			put("THEN","Y");
			put("NUMBER","Y");
			put("IDENTIFIED","Y");
			put("CLUSTER","Y");
			put("WHERE","Y");
			put("SET","Y");
			put("SIZE","Y");
			put("DELETE","Y");
			put("DATE","Y");
			put("VARCHAR2","Y");
			put("NOWAIT","Y");
			put("IS","Y");
			put("RENAME","Y");
			put("ALL","Y");
			put("EXCLUSIVE","Y");
			put("NOT","Y");
			put("INDEX","Y");
			put("INTERSECT","Y");
			put("NOCOMPRESS","Y");
		}
	};
	
	public static boolean isReservedWord(String key){
		return "Y".equals(RESERVED_MAP.get(key.toUpperCase()));
	}
	
	public static String reservedWord(String key) {
		if(isReservedWord(key)) {
			return key+"_";
		} else { 
			return key;
		}
	}
}
