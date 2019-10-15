package com.oracle.objstorage.db;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DBTableData {
	private List<String> datas = new ArrayList<String>();
	private List<String> dataTypes = new ArrayList<String>();

	public void addNumericData(String data) {
		datas.add(data);
		dataTypes.add("Double");
	}

	public void addStringData(String data) {
		datas.add(data);
		dataTypes.add("String");
	}

	public void addDateData(String data) {
		datas.add(formatDate(data));
		dataTypes.add("Date");
	}

	public void addBooleanData(String data) {
		datas.add(data);
		dataTypes.add("Boolean");
	}

	public List<String> getDatas() {
		return datas;
	}

	public void setDatas(List<String> datas) {
		this.datas = datas;
	}

	public List<String> getDataTypes() {
		return dataTypes;
	}

	public void setDataTypes(List<String> dataTypes) {
		this.dataTypes = dataTypes;
	}
	
	private String formatDate(String string) {

		SimpleDateFormat resultSdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		SimpleDateFormat resultSdfdate = new SimpleDateFormat("yyyy-MM-dd");
		if (string != null) {
			if (string.contains("CST")) {
				long d2 = Date.parse(string);
				Date datetime = new Date(d2);
				return resultSdf.format(datetime);
 
			} else if (string.contains("Z")) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'hh:mm:ss'.'sss'Z'");
				java.util.Date datetime;
				try {
					datetime = sdf.parse(string);
					return resultSdf.format(datetime);
					//return (Date) datetime;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
 
			} else if (string.contains("-")&&string.contains(":")) {
				Date newDate;
				try {
					newDate = resultSdf.parse(string);
					return resultSdf.format(newDate);
					//return newDate;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(string.contains("-")&&!string.contains(":")){
				Date newDate;
				try {
					newDate = resultSdfdate.parse(string);
					//return newDate;
					return resultSdf.format(newDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				try {
					DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
					Date date = (Date)formatter.parse(string);
					return resultSdf.format(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				Date longDate = new Date(Long.parseLong(string));
//				return resultSdf.format(longDate);
				//return longDate;
			}
		}
		return null;
	}
}
