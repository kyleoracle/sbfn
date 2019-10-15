package com.oracle.objstorage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetNamespaceResponse;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.oracle.objstorage.db.DBTable;
import com.oracle.objstorage.excel.ExcelReader;

public class ObjectStorageUtil {

	private ObjectStorage client = null;
	private String configurationFilePath = "";
	private String profile = "DEFAULT";
	private String bucketName = "";
	private String bjectFileName = "";
	private String compartmentOCID = "";
	private Region region = null;

	public ObjectStorageUtil() {
	}

	public ObjectStorageUtil(String configurationFilePath, String bucketName, String compartmentOCID,
			String bjectFileName, Region region) {
		super();
		this.configurationFilePath = configurationFilePath;
		this.bucketName = bucketName;
		this.compartmentOCID = compartmentOCID;
		this.bjectFileName = bjectFileName;
		this.region = region;
	}

	private InputStream connect() throws IOException {
		AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configurationFilePath,
				profile);
		client = new ObjectStorageClient(provider);
		client.setRegion(this.region);

		GetNamespaceResponse namespaceResponse = client
				.getNamespace(GetNamespaceRequest.builder().compartmentId(compartmentOCID).build());
		String namespaceName = namespaceResponse.getValue();

		GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucketName(bucketName)
				.namespaceName(namespaceName).objectName(bjectFileName).build();
		GetObjectResponse response = client.getObject(getObjectRequest);
		return response.getInputStream();
	}

	public DBTable exportDBSchema() throws IOException {

		InputStream input = connect();
        ExcelReader reader = new ExcelReader();
        reader.initWorkBook(input);
        
        String sheetName = reader.getSheetsNames().iterator().next();
        List<DataStruct> Head = reader.getHeaderBySheetName(sheetName);
        List<List<DataStruct>> data = reader.getAllDatasBySheetName(sheetName);
        DBTable table = new DBTable(sheetName);
        table.generateDBTableSchema(Head);
        table.generateDBTableData(data);
        
        return table;
 
	}
	
	

	public void destory() throws Exception {
		client.close();
	}
}
