package io.fnproject.example;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.drew.imaging.ImageProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.bmc.Region;
import com.oracle.objstorage.ObjectStorageUtil;
import com.oracle.objstorage.db.DBTable;

import io.cloudevents.CloudEvent;

public class CreateEmployeeFunction {

	private Connection conn = null;
	private String message = "ok";
	public CreateEmployeeFunction() {
		try {

			String dbUser = System.getenv().getOrDefault("DB_USER", "xxx");
			System.err.println("DB User " + dbUser);

			String dbPasswd = System.getenv().getOrDefault("DB_PASSWORD", "xxx");

			String dbServiceName = System.getenv().getOrDefault("DB_SERVICE_NAME", "xxx_medium");
			System.err.println("DB Service name " + dbServiceName);

			String tnsAdminLocation = "/function/wallet";
			// tnsAdminLocation = "/Users/kyle/workspace/fn_adw/Wallet_SR081400ADW";

			System.err.println("TNS Admin location " + tnsAdminLocation);

			String dbUrl = "jdbc:oracle:thin:@" + dbServiceName + "?TNS_ADMIN=" + tnsAdminLocation;
			System.err.println("DB URL " + dbUrl);

			Properties prop = new Properties();

			prop.setProperty("user", dbUser);
			prop.setProperty("password", dbPasswd);

			System.err.println("Connecting to Oracle ATP DB......");

			conn = DriverManager.getConnection(dbUrl, prop);
			if (conn != null) {
				System.err.println("Connected to Oracle ATP DB successfully");
			}

		} catch (Throwable e) {
			System.err.println("DB connectivity failed due - " + e.getMessage());
			message = e.getMessage();
		}
	}

	public String handle(CloudEvent event) throws IOException, ImageProcessingException {
		long startTime = new Date().getTime();
		PreparedStatement st = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map data = objectMapper.convertValue(event.getData().get(), Map.class);
			Map additionalDetails = objectMapper.convertValue(data.get("additionalDetails"), Map.class);

			// check connection to adw
			if (conn == null) {
				System.err.println("Warning: JDBC connection was 'null'");
				return message + " connection is null";
			}

			// get excel
			String bucketName = additionalDetails.get("bucketName").toString();
			System.err.println("bucketName=" + bucketName);

			String compartmentId = data.get("compartmentId").toString();
			System.err.println("compartmentId=" + compartmentId);

			String resourceName = data.get("resourceName").toString();
			System.err.println("resourceName=" + resourceName);

			ObjectStorageUtil osu = new ObjectStorageUtil("/function/oci/config", bucketName, compartmentId,
					resourceName, Region.US_ASHBURN_1);

			DBTable table = osu.exportDBSchema();
			System.err.println(table.getSchema());

			try {
				System.err.println("start drop table ="+table.getSchema().getDropTableMetaData());
				st = conn.prepareStatement(table.getSchema().getDropTableMetaData());
				st.executeUpdate();
			} catch(Exception e) {
				
			}
			// create table
			System.err.println("start create table = "+table.getSchema().getTableMetaData());
			st = conn.prepareStatement(table.getSchema().getTableMetaData());
			st.executeUpdate();
			// insert data
			int count = 0;
			for (String insert : table.getInsertAllIntoStatment()) {
				st = conn.prepareStatement(insert);
				count += st.executeUpdate();
			}
			System.err.println("totaly insert "+count+" times");
		} catch (Exception e) {
			e.printStackTrace();
			return message + e.getMessage();
		} finally {
			try {
				st.close();
			} catch (Exception ignore) {}
		}
		System.err.println("Funtion runs in "+(new Date().getTime()-startTime)/1000+" seconds.");
		return message;
	}
}
