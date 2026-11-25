package com.shop.backend.cart.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CartItemDeleteRequest {

    private List<Integer> cart_item_ids;
}