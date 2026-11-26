
package com.shop.backend.review.controller;

import com.shop.backend.review.dto.ReviewPageResponseDto;
import com.shop.backend.review.service.ReviewService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.shop.backend.review.dto.ReviewCreateRequest;
import com.shop.backend.review.entity.Review;
import com.shop.backend.global.security.JwtUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

import java.util.List;

@RestController // 아래 클래스가 Rest API 요청을 처리함
@RequiredArgsConstructor // final 필드를 생성자로 주입
@RequestMapping("/api/products") // 이 컨트롤러의 공통 URL 지정
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtUtil jwtUtil;
    // 후기 조회에 필요한 실제 작업을 ReviewService에 맡기기 위해 객체 주입
    @GetMapping("/{productId}/reviews")
    // GET /api/products/1/reviews 요청이 들어오면 {productId}에 해당하는 1을 가져옴.
    public ReviewPageResponseDto getReviews(
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "0") int page
            // page라는 Query parameter를 받음
            // page값을 생략하면 기본값 0

    ) {
        return reviewService.getReviewItems(productId, page);
        // 조회된 후기 목록 반환
    }
    @PostMapping("/api/reviews")
    public ResponseEntity<Map<String, String>> createReview(
            @RequestHeader("Authorization") String authorization,
            @ModelAttribute ReviewCreateRequest request
    ) {
        try {
            // 1. Authorization 헤더 확인
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                throw new IllegalArgumentException("로그인이 필요합니다.");
            }

            // 2. JWT 토큰 추출
            String token = authorization.substring(7);

            // 3. JWT에서 로그인 사용자 ID 추출
            String userId = jwtUtil.extractClaims(token);

            // 4. 후기 작성 처리
            reviewService.createReviewWithValidation(request, userId);

            // 5. 성공 응답
            return ResponseEntity.ok(Map.of("code", "ok"));

        } catch (Exception e) {
            // 실패 응답
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", "no"));
        }
    }

}
