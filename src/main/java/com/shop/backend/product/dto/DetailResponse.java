package com.shop.backend.product.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class DetailResponse {
    private String code;

    @JsonUnwrapped
    private ProductDetailResponse detail;
}
