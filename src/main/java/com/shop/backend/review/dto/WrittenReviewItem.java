package com.shop.backend.review.dto;

import java.util.List;

public record WrittenReviewItem(
        int review_id,
        int product_id,
        String product_name,
        int rating,
        String content,
        String created_at,
        List<String> images
) {}
