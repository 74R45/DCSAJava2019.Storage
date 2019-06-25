package com.x74R45.storage;

import static org.junit.Assert.*;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.x74R45.storage.server.Category;
import com.x74R45.storage.server.HttpServerLauncher;

public class HttpServerTest {

	@BeforeClass
	public static void setup() {
		try {
			HttpServerLauncher.main(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test() {
		String token = testLogin();
		Category test = new Category(0, "testCategory567", null);
		int category_id = testPutCategory(test, token);
		test.setId(category_id);
		testGetCategory(test, token);
		testDeleteCategory(category_id, token);
	}
	
	@SuppressWarnings("unchecked")
	private String testLogin() {
		try {
			String login = "login", pass = DigestUtils.sha256Hex("password");
			URL url = new URL("http://localhost:7474/login");
			
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			JSONObject jobj = new JSONObject();
			jobj.put("login", login);
			jobj.put("password", pass);
			wr.writeBytes(jobj.toString());
			wr.flush();
			wr.close();
			
			int responseCode = con.getResponseCode();
			assertEquals(200, responseCode);
			return new String(con.getInputStream().readAllBytes(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			fail("an exception has been thrown");
		}
		return null;
	}
	
	private int testPutCategory(Category c, String token) {
		try {
			URL url = new URL("http://localhost:7474/categories");
			
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Authorization", token);
			con.setRequestMethod("PUT");
			
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(c.toJSON().toString());
			wr.flush();
			wr.close();
			
			int responseCode = con.getResponseCode();
			assertEquals(201, responseCode);
			return Integer.parseInt(new String(con.getInputStream().readAllBytes(), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("an exception has been thrown");
		}
		return 0;
	}
	
	private void testGetCategory(Category c, String token) {
		try {
			URL url = new URL("http://localhost:7474/categories");
			
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Authorization", token);
			con.setRequestMethod("GET");
			
			int responseCode = con.getResponseCode();
			assertEquals(200, responseCode);
			String categories = new String(con.getInputStream().readAllBytes(), "UTF-8");
			assertTrue(categories.contains("\"category_name\":\"" + c.getName() + '"'));
		} catch (Exception e) {
			e.printStackTrace();
			fail("an exception has been thrown");
		}
	}
	
	private void testDeleteCategory(int id, String token) {
		try {
			URL url = new URL("http://localhost:7474/categories/" + id);
			
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Authorization", token);
			con.setRequestMethod("DELETE");
			
			int responseCode = con.getResponseCode();
			assertEquals(200, responseCode);
		} catch (Exception e) {
			e.printStackTrace();
			fail("an exception has been thrown");
		}
	}
	
	@AfterClass
	public static void shutdown() {
		HttpServerLauncher.stop();
	}
}