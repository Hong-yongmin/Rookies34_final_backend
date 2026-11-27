package com.shop.backend.seller.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shop.backend.product.dto.PackagingType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SellerProductUpdateRequest {
    private String name;
    private Long category_id;
    private Integer price;
    private Integer discount_rate;
    private Integer stock;
    private PackagingType storage_type;
    private String origin;
    private String description;
}
