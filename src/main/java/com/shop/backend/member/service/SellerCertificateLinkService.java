package com.shop.backend.member.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class SellerCertificateLinkService {

    private final SecretKey signingKey = Jwts.SIG.HS256.key().build();

    public String createLink(String sellerId) {
        String token = Jwts.builder()
                .subject(sellerId)
                .expiration(Date.from(Instant.now().plus(Duration.ofMinutes(5))))
                .signWith(signingKey)
                .compact();
        return "/api/seller/cert-file?download_token=" + token;
    }

    public String getSellerId(String token) {
        try {
            return Jwts.parser().verifyWith(signingKey).build()
                    .parseSignedClaims(token).getPayload().getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 다운로드 링크입니다.");
        }
    }
}
