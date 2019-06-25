package com.x74R45.storage.server;

import java.util.ArrayList;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class TokenStorage {

	public static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	private static volatile ArrayList<String> tokens = new ArrayList<>();
	
	public static void saveToken(String token) {
		if (validateToken(token))
			removeToken(token);
		
		synchronized (tokens) {
			tokens.add(token);
		}
	}
	
	public static boolean validateToken(String token) {
		if (!tokens.contains(token))
			return false;
		
		Claims claims = Jwts.parser()
				.setSigningKey(SECRET_KEY.getEncoded())
				.parseClaimsJws(token).getBody();
		
		if (claims.getExpiration().getTime() - System.currentTimeMillis() < 0) {
			removeToken(token);
			return false;
		}
		return true;
	}
	
	private static void removeToken(String token) {
		synchronized (tokens) {
			tokens.remove(token);
		}
	}
}