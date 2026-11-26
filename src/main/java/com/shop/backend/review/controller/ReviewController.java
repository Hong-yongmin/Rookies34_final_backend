
package com.shop.backend.review.controller;

import com.shop.backend.review.dto.ReviewItemDto;
import com.shop.backend.review.service.ReviewService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{productId}/reviews")
    public List<ReviewItemDto> getReviews(
            @PathVariable Integer productId
    ) {

        return reviewService.getReviewItems(productId);

    }

}
