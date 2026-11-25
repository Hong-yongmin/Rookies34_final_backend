package com.shop.backend.cart.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemsRequest {
    private Integer product_id;
    @Min(1)
    @Max(99)
    private Integer quantity;
}
