package com.shop.backend.review.dto;

public record WritableReviewItem(
        Integer order_item_id,
        Integer order_id,
        String name,
        String ordered_at
) {}
