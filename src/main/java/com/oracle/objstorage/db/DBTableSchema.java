package com.oracle.objstorage.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DBTableSchema {
	private List<DBColumn> columns    = new ArrayList<DBColumn>();
	private String         tableName = "";
	
	public void AddColumn(String name, String type) {
		name = DBUtil.reservedWord(name);
		DBColumn column = new DBColumn(name,type,"",false,0);
		columns.add(column);
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	private void AddColumn(String name, String type, String defaultValue, boolean isPK, int pkSeq) {
		name = DBUtil.reservedWord(name);
		if(pkSeq <=0) {
			pkSeq = 0;
		}
		DBColumn column = new DBColumn(name,type,defaultValue,isPK,pkSeq);
		columns.add(column);
	}
	
	public List<String> getColumns() {
		List<String> result = new ArrayList<>();
		for(DBColumn db: columns) {
			result.add(db.getColumnName());
		}
		return result;
	}
	
	private class DBColumn implements Comparable<DBColumn>{
		public DBColumn() {
			super();
		}
		public DBColumn(String columnName, String columnType, String defaultValue, boolean isPK, int pkSeq) {
			super();
			ColumnName = columnName;
			ColumnType = columnType;
			this.defaultValue = defaultValue;
			this.isPK = isPK;
			this.pkSeq = pkSeq;
		}
		private String  ColumnName   = "";
		private String  ColumnType   = "";
		private String  defaultValue = "";
		private boolean isPK         = false;
		private int     pkSeq        = 0;
		public String getColumnName() {
			return ColumnName;
		}
		public void setColumnName(String columnName) {
			ColumnName = columnName;
		}
		public String getColumnType() {
			return ColumnType;
		}
		public void setColumnType(String columnType) {
			ColumnType = columnType;
		}
		public String getDefaultValue() {
			return defaultValue;
		}
		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}
		public boolean isPK() {
			return isPK;
		}
		public void setPK(boolean isPK) {
			this.isPK = isPK;
		}
		public int getPkSeq() {
			return pkSeq;
		}
		public void setPkSeq(int pkSeq) {
			this.pkSeq = pkSeq;
		}
		@Override
		public int compareTo(DBColumn o) {
			return Integer.compare(this.pkSeq, o.pkSeq);
		}
	}
	
	public static class DBColumnType {
		private static final String VARCHAR2 = "VARCHAR2";
		private static final String NUMBER   = "NUMBER";
		private static final String DATE     = "DATE";
		private static final String BOOLEAN  = "BOOL";
		
		public static String number(){
			return NUMBER;
		}
		public static String number(int a,int b) {
			return NUMBER+"("+a+","+b+")";
		}
		
		public static String varchar2(int b) {
			if(b > 4000) {
				b = 4000;
			}
			if(b <=0) {
				b = 1000;
			}
			return VARCHAR2+"("+b+")";
		}
		public static String varchar2() {
			return VARCHAR2+"(1000)";
		}
		public static String date() {
			return DATE;
		}
		public static String bool() {
			return BOOLEAN;
		}
	}
	
	public String getTableMetaData() {
		String rstBegin = "CREATE TABLE "+tableName+" (";
		String rstEnd   = ")";
		String comma    = " , ";
		String separ    = " ";
		StringBuilder sb = new StringBuilder();

		Iterator<DBColumn> it = columns.iterator();
		while(it.hasNext()) {
			DBColumn dbc = it.next();
			sb.append(dbc.getColumnName())
			  .append(separ)
			  .append(dbc.getColumnType())
			  .append(separ)
			  .append(dbc.defaultValue)
			  .append(comma);
		}
		String tmp = sb.toString();
		int lastIndex = tmp.lastIndexOf(",");
		return rstBegin+tmp.substring(0,lastIndex)+rstEnd;
	}
	
	public String getDropTableMetaData() {
		return "DROP TABLE "+tableName;
	}
	
	public DBTableSchema() {
	}
	public DBTableSchema(String tableName) {
		this.tableName = tableName;
	}
}
