package com.x74R45.storage.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

	private static final String PASS_PATH = "dbpass.txt";
	private static Connection con;
	private static boolean isConnected = false;
	
	public static void initialize() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/storage", "root", getPassword());
			isConnected = true;
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() {
		return con;
	}
	
	public static boolean isConnected() {
		return isConnected;
	}
	
	private static String getPassword() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(PASS_PATH));
		String res = reader.readLine();
		reader.close();
		return res;
	}
}