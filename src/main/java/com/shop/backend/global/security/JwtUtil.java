package com.shop.backend.global.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;

public class JwtUtil {

    public String generateToken(String id){
        return Jwts.builder()
                .subject(id)
                .compact(); // id를 이용하여 서명 없이 JWT 토큰 생성
    }

    public String extractClaims(String token){
        Claims claims = Jwts.parser()
                .unsecured()
                .build()
                .parseUnsecuredClaims(token)
                .getPayload();

        return claims.getSubject(); // JWT 토큰에서 id를 추출하여 반환
    }
}
