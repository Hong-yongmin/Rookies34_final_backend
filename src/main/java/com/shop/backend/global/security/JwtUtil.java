package com.shop.backend.global.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;

public class JwtUtil {

    public String generateToken(String id){
        return Jwts.builder()
                .subject(id)
                .compact();
    }

    public String extractClaims(String token){
        Claims claims = Jwts.parser()
                .unsecured()
                .build()
                .parseUnsecuredClaims(token)
                .getPayload();
        return claims.getSubject();
    }
}
