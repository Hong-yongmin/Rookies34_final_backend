
package com.shop.backend.review.controller;

import com.shop.backend.review.dto.ReviewPageResponseDto;
import com.shop.backend.review.service.ReviewService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 아래 클래스가 Rest API 요청을 처리함
@RequiredArgsConstructor // final 필드를 생성자로 주입
@RequestMapping("/api/products") // 이 컨트롤러의 공통 URL 지정
public class ReviewController {

    private final ReviewService reviewService;
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


}
