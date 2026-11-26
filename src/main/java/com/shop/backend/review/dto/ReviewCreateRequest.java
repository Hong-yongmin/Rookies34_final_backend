package com.shop.backend.review.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCreateRequest {

    private Integer order_item_id;

    private Integer rating;

    private String content;
}