package com.x74R45.storage.server;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import com.x74R45.storage.database.DBConnector;
import com.x74R45.storage.handlers.CategoryHandler;
import com.x74R45.storage.handlers.LoginHandler;
import com.x74R45.storage.handlers.ProductHandler;

public class HttpsServerLauncher {

	private static HttpsServer server;
	private static final int PORT = 7474;
	private static final int NTHREADS = 100;
	private static final Executor exec = Executors.newFixedThreadPool(NTHREADS);
	
	public static void main(String[] args) throws Exception {
		DBConnector.initialize();
		
		// Initialise the server
		server = HttpsServer.create(new InetSocketAddress(PORT), 0);
		SSLContext sslContext = SSLContext.getInstance("TLS");
		
		// Initialise the keystore
		char[] password = "password".toCharArray();
		KeyStore ks = KeyStore.getInstance("JKS");
		FileInputStream fis = new FileInputStream("key.jks");
		ks.load(fis, password);
		
		// Setup the key manager factory
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(ks, password);

		// Setup the trust manager factory
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(ks);
        
		// Setup the HTTPS context and parameters
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		server.setHttpsConfigurator (new HttpsConfigurator(sslContext) {
			public void configure(HttpsParameters params) {
				try {
					SSLContext c = SSLContext.getDefault();
					SSLEngine engine = c.createSSLEngine();
					params.setNeedClientAuth(false);
					params.setCipherSuites(engine.getEnabledCipherSuites());
					params.setProtocols(engine.getEnabledProtocols());

					SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
					params.setSSLParameters(defaultSSLParameters);
				} catch (Exception e) {
					e.printStackTrace();
				}
		     }
		 });

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