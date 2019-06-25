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
import com.x74R45.storage.server.Product;
import com.x74R45.storage.server.TokenStorage;

public class ProductHandler implements HttpHandler {

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
			getProduct(exchange);
			break;
		case "POST":
			postProduct(exchange);
			break;
		case "PUT":
			putProduct(exchange);
			break;
		case "DELETE":
			deleteProduct(exchange);
			break;
		}
	}
	
	private void getProduct(HttpExchange exchange) throws IOException {
		try {
			ArrayList<Product> products = DBInteractor.getAllProducts();
			String response = "[";
			for (Product pr : products) 
				response += pr.toJSON().toString() + ',';
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
	
	private void postProduct(HttpExchange exchange) throws IOException {
		String json = new String(exchange.getRequestBody().readAllBytes());
		JSONParser parser = new JSONParser();
		
		try {
			JSONObject jobj = (JSONObject) parser.parse(json);
			Product pr = new Product(
					(int)(long)jobj.get("product_id"),
					(String)jobj.get("product_name"),
					(String)jobj.get("product_description"),
					(String)jobj.get("product_maker"),
					(int)(long)jobj.get("product_amount"), 
					(String)jobj.get("product_price"),
					(int)(long)jobj.get("category_id"));
			if (!pr.isValid()) {
				exchange.sendResponseHeaders(409, "Bad Request".length());
		        OutputStream os = exchange.getResponseBody();
		        os.write("Bad Request".getBytes());
		        os.close();
		        return;
			}
			
			try {
				DBInteractor.changeProduct(pr);
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

	private void putProduct(HttpExchange exchange) throws IOException {
		String json = new String(exchange.getRequestBody().readAllBytes());
		JSONParser parser = new JSONParser();
		
		try {
			JSONObject jobj = (JSONObject) parser.parse(json);
			Product pr = new Product(0,
					(String)jobj.get("product_name"),
					(String)jobj.get("product_description"),
					(String)jobj.get("product_maker"),
					(int)(long)jobj.get("product_amount"), 
					(String)jobj.get("product_price"),
					(int)(long)jobj.get("category_id"));
			if (!pr.isValid()) {
				exchange.sendResponseHeaders(409, "Bad Request".length());
		        OutputStream os = exchange.getResponseBody();
		        os.write("Bad Request".getBytes());
		        os.close();
		        return;
			}
			
			String id = Integer.toString(DBInteractor.createProduct(pr));
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

	private void deleteProduct(HttpExchange exchange) throws IOException {
		try {
			DBInteractor.deleteProduct(getId(exchange));
			
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