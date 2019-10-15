package com.oracle.objstorage.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.oracle.objstorage.DataStruct;

public class DBTable {
	private String tableName = "";
	private List<DBTableData> data = new ArrayList<DBTableData>();
	private DBTableSchema schema = new DBTableSchema();

	public void generateDBTableSchema(List<DataStruct> dsList) {
		if (dsList == null || dsList.isEmpty())
			return;
		Collections.sort(dsList);
		for (DataStruct ds : dsList) {
			String type = "";
			if (ds.getType().equals("String")) {
				type = DBTableSchema.DBColumnType.varchar2();
			} else if (ds.getType().equals("Date")) {
				type = DBTableSchema.DBColumnType.date();
			} else if (ds.getType().equals("Boolean")) {
				type = DBTableSchema.DBColumnType.bool();
			} else if (ds.getType().equals("Double")) {
				type = DBTableSchema.DBColumnType.number();
			}
			schema.AddColumn(ds.getValue(), type);
		}
	}

	public List<String> getInsertStatment() {
		String insert = getInsertColumsStmt();
		List<String> list = getInsertValuesStmts();
		List<String> result = new ArrayList<String>();
		for (String stmt : list) {
			result.add(insert + stmt);
		}
		return result;
	}
	
	public List<String> getInsertAllIntoStatment() {
		String begin = "INSERT ALL ";
		String end = " SELECT 1 FROM DUAL ";
		String insert = getInsertAllIntoColumsStmt();
		List<String> list = getInsertValuesStmts();
		List<String> result = new ArrayList<String>();
		int size = list.size();
		if (size <= 1000) {
			StringBuilder sb = new StringBuilder();
			for (String stmt : list) {
				sb.append(insert).append(" ").append(stmt).append(" ");
			}
			String tmpRst = begin + sb.toString() + end;
			result.add(tmpRst);
		} else {
			int count = 0;
			StringBuilder sb = new StringBuilder();
			for (String stmt : list) {
				count++;
				sb.append(insert).append(" ").append(stmt).append(" ");
				if(count == 999) {
					String tmpRst = begin + sb.toString() + end;
					result.add(tmpRst);
					count = 0;
					sb = new StringBuilder();
				}
			}
			if(count != 0) {
				String tmpRst = begin + sb.toString() + end;
				result.add(tmpRst);
			}
		}
		return result;
	}
	
	
	private String getInsertColumsStmt() {
		List<String> list = schema.getColumns();
		StringBuilder sb = new StringBuilder();
		String BEGIN = "INSERT INTO " + tableName + " ( ";
		String COMMA = " , ";
		String END = " ) ";
		sb.append(BEGIN);
		for (String column : list) {
			sb.append(column).append(COMMA);
		}
		String returnValue = sb.toString();
		int lastIndex = returnValue.lastIndexOf(",");
		returnValue = returnValue.substring(0, lastIndex);
		return returnValue + END;
	}

	private String getInsertAllIntoColumsStmt() {
		List<String> list = schema.getColumns();
		StringBuilder sb = new StringBuilder();
		String BEGIN = "INTO " + tableName + " ( ";
		String COMMA = " , ";
		String END = " ) ";
		sb.append(BEGIN);
		for (String column : list) {
			sb.append(column).append(COMMA);
		}
		String returnValue = sb.toString();
		int lastIndex = returnValue.lastIndexOf(",");
		returnValue = returnValue.substring(0, lastIndex);
		return returnValue + END;
	}

	private List<String> getInsertValuesStmts() {
		List<String> result = new ArrayList<String>();
		for (DBTableData row : data) {
			List<String> datas = row.getDatas();
			List<String> dataTypes = row.getDataTypes();

			String COMMA = " , ";
			String BEGIN = " VALUES (";
			String END = ")";
			StringBuilder sb = new StringBuilder();
			sb.append(BEGIN);
			for (int i = 0; i < datas.size(); i++) {
				String data = datas.get(i);
				String type = dataTypes.get(i);
				if ("String".equals(type)) {
					sb.append("'" + data + "'");
				} else if ("Double".equals(type) || "Boolean".equals(type)) {
					sb.append(data);
				} else if ("Date".equals(type)) {
					sb.append("to_date('" + data + "','yyyy-mm-dd HH24:mi:ss')");
				} // else if("Boolean".equals(type)) {
					//
					// }
				sb.append(COMMA);
			}
			String returnValue = sb.toString();
			
			int lastIndex = returnValue.lastIndexOf(",");
			if(lastIndex <= 0) {
				continue;
			}
				
			returnValue = returnValue.substring(0, lastIndex);
			returnValue = returnValue + END;
			result.add(returnValue);
		}
		return result;
	}

	public void generateDBTableData(List<List<DataStruct>> dsList) {
		if (dsList == null || dsList.isEmpty())
			return;
		for (List<DataStruct> ds : dsList) {
			DBTableData tableData = new DBTableData();
			for (DataStruct dt : ds) {
				if (dt.getType().equals("String")) {
					tableData.addStringData(dt.getValue());
				} else if (dt.getType().equals("Double")) {
					tableData.addNumericData(dt.getValue());
				} else if (dt.getType().equals("Date")) {
					tableData.addDateData(dt.getValue());
				} else if (dt.getType().equals("boolean")) {
					tableData.addStringData(dt.getValue());
				}
			}
			data.add(tableData);
		}
	}

	public DBTable() {
	}
 
	public DBTable(String tableName) {
		this.tableName = tableName;
		schema.setTableName(this.tableName);
	}
 
	public String getTableName() {
		return tableName;
	}
 
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
 
	public List<DBTableData> getData() {
		return data;
	}
 
	public void setData(List<DBTableData> data) {
		this.data = data;
	}
 
	public DBTableSchema getSchema() {
		return schema;
	}
 
	public void setSchema(DBTableSchema schema) {
		this.schema = schema;
	}
}
