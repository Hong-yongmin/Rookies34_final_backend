package com.shop.backend.support.inquiry.service;

import io.jsonwebtoken.Claims;
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
public class InquiryAttachmentLinkService {

    private static final Duration VALIDITY = Duration.ofMinutes(5);

    private final SecretKey signingKey = Jwts.SIG.HS256.key().build();

    public String createLink(Integer inquiryId, String userId) {
        String token = Jwts.builder()
                .subject(userId)
                .claim("inquiry_id", inquiryId)
                .expiration(Date.from(Instant.now().plus(VALIDITY)))
                .signWith(signingKey)
                .compact();

        return "/api/inquiries/" + inquiryId + "/attachment?download_token=" + token;
    }

    public String getOwner(Integer inquiryId, String token) {
        if (token != null && !token.isBlank()) {
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(signingKey)
                        .require("inquiry_id", inquiryId)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
                return claims.getSubject();
            } catch (JwtException | IllegalArgumentException e) {
                throw invalidLink();
            }
        }
        throw invalidLink();
    }

    private ResponseStatusException invalidLink() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired attachment link");
    }
}
