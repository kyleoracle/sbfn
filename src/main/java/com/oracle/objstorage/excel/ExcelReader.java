package com.oracle.objstorage.excel;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.oracle.objstorage.DataStruct;

public class ExcelReader {
	public Workbook workBook = null;
	Map<String,Map<String,List<List<DataStruct>>>> dataSet = null;
	
	public void initWorkBook(InputStream in) throws IOException {
        try {
            workBook = new XSSFWorkbook(in);
        } catch (IOException e) {
            workBook = new HSSFWorkbook(in);
        }
        dataSet = getData();
    }
	
	public void closeWorkBook() throws IOException {
		if(workBook != null) {
			workBook.close();
		}
	}
	
	public Set<String> getSheetsNames(){
		if(dataSet != null) {
			return dataSet.keySet();
		}
		return null;
	}
	
	public List<DataStruct> getHeaderBySheetName(String sheetName){
		Map<String,List<List<DataStruct>>> tmp = dataSet.get(sheetName);
		if(tmp != null) {
			List<List<DataStruct>> rstSet = tmp.get("header");
			if(rstSet != null && !rstSet.isEmpty()) {
				return rstSet.get(0);
			}
		}
		return null;
	}
	
	public List<List<DataStruct>> getAllDatasBySheetName(String sheetName){
		Map<String,List<List<DataStruct>>> tmp = dataSet.get(sheetName);
		if(tmp != null) {
			List<List<DataStruct>> rstSet = tmp.get("data");
			return rstSet;
		}
		return null;
	}
	
	private DataStruct getCellValue(Cell cell,boolean isHeader,int index) {
		
		DataStruct ds = new DataStruct();
		ds.setHeader(isHeader);
		ds.setIndex(index);
		
		String cellValue = "";
		if(cell == null)
			return ds ;
		
		switch (cell.getCellType()) {
		case NUMERIC:{
			if(DateUtil.isCellDateFormatted(cell)) {
				cellValue = String.valueOf(cell.getDateCellValue());
				ds.setType("Date");
				ds.setValue(cellValue);
				break;
			}
			cellValue = new BigDecimal(new Double(cell.getNumericCellValue())).toString();
			ds.setType("Double");
			ds.setValue(cellValue);
			break;
		}
		case STRING:{
			cellValue = cell.getStringCellValue();
			ds.setType("String");
			ds.setValue(cellValue);
			break;
		}
		case BOOLEAN:{
			cellValue = String.valueOf(cell.getBooleanCellValue());
			ds.setType("Boolean");
			ds.setValue(cellValue);
			break;
		}
		case FORMULA:{
			cellValue = "";
			break;
		}
		case BLANK: {
			cellValue = "";
			break;
		}
		default:
			break;
		}
		return ds;
	}
	
	public Map<String,Map<String,List<List<DataStruct>>>> getData(){
		Map<String,Map<String,List<List<DataStruct>>>> AllData = new HashMap<String,Map<String,List<List<DataStruct>>>>();
		
        int size = workBook.getNumberOfSheets();
		for (int i = 0; i <size; i++) {
			Sheet sheet = workBook.getSheetAt(i);
			String SheetName = sheet.getSheetName();
			
			List<DataStruct> headers = new ArrayList<DataStruct>();
			List<List<DataStruct>> dataMatrix = new ArrayList<List<DataStruct>>();
			
			 int rowNumber = sheet.getPhysicalNumberOfRows();
			 for (int rowIndex = 0; rowIndex < rowNumber; rowIndex++) {
				Row row = sheet.getRow(rowIndex);
				int cellNums = row.getPhysicalNumberOfCells();
				List<DataStruct> datas = new ArrayList<DataStruct>();
				for (int cellIndex = 0; cellIndex < cellNums; cellIndex++) {
					Cell cell = row.getCell(cellIndex);
					boolean isHeader = (rowIndex == 0?true:false);
					DataStruct ds = getCellValue(cell,isHeader,cellIndex);
					if(isHeader) {
						headers.add(ds);
					} else {
						datas.add(ds);
					}
					if(rowIndex == 1) {
						headers.get(cellIndex).setType(ds.getType());
					}
				}
				Collections.sort(datas);
				if(rowIndex != 0) {
					dataMatrix.add(datas);
				}
			}
			Collections.sort(headers);
			List<List<DataStruct>> temp = new ArrayList<List<DataStruct>>();
			temp.add(headers);
			Map<String,List<List<DataStruct>>> tmpData = new HashMap<String,List<List<DataStruct>>>();
			tmpData.put("header",temp);
			tmpData.put("data",dataMatrix);
			AllData.put(SheetName, tmpData);
		}
		return AllData;
	}
}
