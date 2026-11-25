package com.shop.backend.global.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import org.springframework.http.HttpHeaders;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String MEMBER_ID = "memberid";

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException{

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                String id = jwtUtil.extractClaims(token);
                request.setAttribute(MEMBER_ID, id);
            } catch (JwtException | IllegalArgumentException e) {
                // 형식이 잘못된 토큰은 비로그인 상태로 처리
            }
        }
        chain.doFilter(request, response); // 다음 필터/컨트롤러로 요청 전달
    }
}
