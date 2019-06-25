package com.x74R45.storage.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import com.sun.net.httpserver.HttpServer;
import com.x74R45.storage.database.DBConnector;
import com.x74R45.storage.handlers.CategoryHandler;
import com.x74R45.storage.handlers.LoginHandler;
import com.x74R45.storage.handlers.ProductHandler;

public class HttpServerLauncher {

	private static HttpServer server;
	private static final int PORT = 7474;
	private static final int NTHREADS = 100;
	private static final Executor exec = Executors.newFixedThreadPool(NTHREADS);
	
	public static void main(String[] args) throws Exception {
		server = HttpServer.create(new InetSocketAddress(PORT), 0);
		
		DBConnector.initialize();
		
		server.createContext("/login", new LoginHandler());
		server.createContext("/products", new ProductHandler());
		server.createContext("/categories", new CategoryHandler());
		server.setExecutor(exec);
		server.start();
		
		System.out.println("Server has started.");
	}
	
	public static void stop() {
		server.stop(1);
	}
}