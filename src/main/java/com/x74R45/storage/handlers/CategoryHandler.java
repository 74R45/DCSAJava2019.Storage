package com.x74R45.storage.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.x74R45.storage.database.DBInteractor;
import com.x74R45.storage.server.Category;
import com.x74R45.storage.server.TokenStorage;

public class CategoryHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		if (LoginHandler.checkOptions(exchange))
			return;
		
		String token = exchange.getRequestHeaders().getFirst("Authorization");
		if (token == null || !TokenStorage.validateToken(token)) {
			exchange.sendResponseHeaders(403, 0);
			OutputStream os = exchange.getResponseBody();
			os.close();
			return;
		}
		
		String method = exchange.getRequestMethod();
		switch (method) {
		case "GET":
			getCategory(exchange);
			break;
		case "POST":
			postCategory(exchange);
			break;
		case "PUT":
			putCategory(exchange);
			break;
		case "DELETE":
			deleteCategory(exchange);
			break;
		}
	}
	
	private void getCategory(HttpExchange exchange) throws IOException {
		try {
			ArrayList<Category> categories = DBInteractor.getAllCategories();
			String response = "[";
			for (Category c : categories) 
				response += c.toJSON().toString() + ',';
			if (response.length() != 1)
				response = response.substring(0, response.length()-1);
			response += ']';
			
			exchange.sendResponseHeaders(200, response.length());
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (SQLException e) {
			e.printStackTrace();
			exchange.sendResponseHeaders(500, "Error".length());
	        OutputStream os = exchange.getResponseBody();
	        os.write("Error".getBytes());
	        os.close();
		}
	}
	
	private void postCategory(HttpExchange exchange) throws IOException {
		String json = new String(exchange.getRequestBody().readAllBytes());
		JSONParser parser = new JSONParser();
		
		try {
			JSONObject jobj = (JSONObject) parser.parse(json);
			Category c = new Category(
					(int)(long)jobj.get("category_id"),
					(String)jobj.get("category_name"),
					(String)jobj.get("category_description"));
			
			try {
				DBInteractor.changeCategory(c);
				exchange.sendResponseHeaders(200, 0);
				OutputStream os = exchange.getResponseBody();
				os.close();
			} catch (SQLException e) {
				e.printStackTrace();
				exchange.sendResponseHeaders(404, "Not Found".length());
		        OutputStream os = exchange.getResponseBody();
		        os.write("Not Found".getBytes());
		        os.close();
			}
		} catch (ParseException e) {
			e.printStackTrace();
			exchange.sendResponseHeaders(409, "Bad Request".length());
	        OutputStream os = exchange.getResponseBody();
	        os.write("Bad Request".getBytes());
	        os.close();
		}
	}

	private void putCategory(HttpExchange exchange) throws IOException {
		String json = new String(exchange.getRequestBody().readAllBytes());
		JSONParser parser = new JSONParser();
		
		try {
			JSONObject jobj = (JSONObject) parser.parse(json);
			Category c = new Category(0,
					(String)jobj.get("category_name"),
					(String)jobj.get("category_description"));
			
			String id = Integer.toString(DBInteractor.createCategory(c));
			exchange.sendResponseHeaders(201, id.length());
			OutputStream os = exchange.getResponseBody();
			os.write(id.getBytes());
			os.close();
		} catch (ParseException | SQLException e) {
			e.printStackTrace();
			exchange.sendResponseHeaders(409, "Bad Request".length());
	        OutputStream os = exchange.getResponseBody();
	        os.write("Bad Request".getBytes());
	        os.close();
		}
	}

	private void deleteCategory(HttpExchange exchange) throws IOException {
		try {
			DBInteractor.deleteCategory(getId(exchange));
			
			exchange.sendResponseHeaders(200, 0);
			OutputStream os = exchange.getResponseBody();
			os.close();
		} catch (SQLException e) {
		      e.printStackTrace();
		      exchange.sendResponseHeaders(404, "Not Found".length());
		      OutputStream os = exchange.getResponseBody();
		      os.write("Not Found".getBytes());
		      os.close();
		}
	}
	
	private int getId(HttpExchange exchange) {		
		return Integer.parseInt(exchange.getRequestURI().toString().split("/")[2]);
	}
}