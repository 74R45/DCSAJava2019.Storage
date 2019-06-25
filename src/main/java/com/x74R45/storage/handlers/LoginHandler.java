package com.x74R45.storage.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.x74R45.storage.database.DBInteractor;
import com.x74R45.storage.server.TokenStorage;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class LoginHandler implements HttpHandler {
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		if (checkOptions(exchange))
			return;
		
		if (exchange.getRequestMethod().equals("POST")) {
			String json = new String(exchange.getRequestBody().readAllBytes());
			JSONParser parser = new JSONParser();

			String login = null, password = null;
			try {
				JSONObject jobj = (JSONObject) parser.parse(json);
				login = (String)jobj.get("login");
				password = (String)jobj.get("password");
				
				if (!DBInteractor.checkUser(login, password)) {
					sendUnauthorized(exchange);
					return;
				}
			} catch (SQLException | ParseException e) {
				e.printStackTrace();
				sendUnauthorized(exchange);
				return;
			}
			
			String token;
			try {
				JwtBuilder builder = Jwts.builder()
						.setId(login)
				        .setIssuedAt(new Date(System.currentTimeMillis()))
				        .setExpiration(new Date(System.currentTimeMillis() + 24*3600000)) // Expires in one hour
				        .signWith(TokenStorage.SECRET_KEY, SignatureAlgorithm.HS256);
				
				token = builder.compact();
				TokenStorage.saveToken(token);
			} catch (Exception e) {
				e.printStackTrace();
				sendUnauthorized(exchange);
				return;
			}
			
			System.out.println("Logged in successfully for user \"" + login + "\".");
			
			exchange.sendResponseHeaders(200, token.length());
			OutputStream os = exchange.getResponseBody();
			os.write(token.getBytes());
			os.close();
		}
	}
	
	private void sendUnauthorized(HttpExchange exchange) throws IOException {
		exchange.sendResponseHeaders(401, 0);
		OutputStream os = exchange.getResponseBody();
		os.close();
	}
	
	public static boolean checkOptions(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
            exchange.sendResponseHeaders(204, -1);
            return true;
        }
        return false;
    }
}