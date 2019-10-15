package com.example.rest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.bmc.Region;
import com.oracle.objstorage.ObjectStorageUtil;
import com.oracle.objstorage.db.DBTable;

import io.cloudevents.impl.DefaultCloudEventImpl;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	private Connection conn = null;
	private String message = null;

	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public AppTest(String testName) {
		super(testName);

		try {

			String dbUser = System.getenv().getOrDefault("DB_USER", "xxx");
			System.err.println("DB User " + dbUser);

			String dbPasswd = System.getenv().getOrDefault("DB_PASSWORD", "xxx");

			String dbServiceName = System.getenv().getOrDefault("DB_SERVICE_NAME", "xxx_medium");
			System.err.println("DB Service name " + dbServiceName);

			String tnsAdminLocation = "/wallet";
			// tnsAdminLocation = "/Users/kyle/workspace/sbfn/wallet";

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

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {

		try {
			ObjectMapper objectMapper = new ObjectMapper();

			DefaultCloudEventImpl<Object> event = objectMapper.readValue(
					"{\"cloudEventsVersion\":\"0.1\",\"eventID\":\"d947848a-4b61-41d5-83b4-c8ab594f7ca3\",\"eventType\":\"com.oraclecloud.objectstorage.createobject\",\"source\":\"objectstorage\",\"eventTypeVersion\":\"2.0\",\"eventTime\":\"2019-01-10T21:19:24Z\",\"contentType\":\"application/json\",\"extensions\":{\"compartmentId\":\"ocid1.compartment.oc1.......unique_id\"},\"data\":{\"compartmentId\":\"ocid1.compartment.oc1..aaaaaaaagsnakmad7tgmqg3aheqqvol2x6i7fj3klso3rvjvzpq2mv2wd26q\",\"compartmentName\":\"example_name\",\"resourceName\":\"MonthlyReport.xlsx\",\"resourceId\":\"ocid1.bucket.oc1.......unique_id\",\"availabilityDomain\":\"all\",\"additionalDetails\":{\"eTag\":\"f8ffb6e9-f602-460f-a6c0-00b5abfa24c7\",\"namespace\":\"example_namespace\",\"bucketName\":\"SR081400-BUCKET\",\"bucketId\":\"ocid1.bucket.oc1......unique_id\",\"archivalState\":\"Available\"}}}",
					new TypeReference<DefaultCloudEventImpl<Object>>() {
					});

			Map data = objectMapper.convertValue(event.getData().get(), Map.class);
			Map additionalDetails = objectMapper.convertValue(data.get("additionalDetails"), Map.class);

			// check connection to adw
			if (conn == null) {
				System.err.println("Warning: JDBC connection was 'null'");
			}

			// get excel
			String bucketName = additionalDetails.get("bucketName").toString();
			System.out.println("bucketName=" + bucketName);
			
			String compartmentId = data.get("compartmentId").toString();
			System.out.println("compartmentId=" + compartmentId);
			
			String resourceName = data.get("resourceName").toString();
			System.out.println("resourceName=" + resourceName);
			
			ObjectStorageUtil osu = new ObjectStorageUtil("/function/oci/config", bucketName, compartmentId,
					resourceName, Region.US_ASHBURN_1);

			DBTable table = osu.exportDBSchema();
			System.out.println(table.getSchema());

			// create table
			PreparedStatement st = conn.prepareStatement(table.getSchema().getTableMetaData());
			st.executeUpdate();

			// insert data
			for (String insert : table.getInsertStatment()) {
				st = conn.prepareStatement(insert);
				st.executeUpdate();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
