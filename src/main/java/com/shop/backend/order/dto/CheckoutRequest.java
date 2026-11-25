package com.shop.backend.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CheckoutRequest { // POST /api/orders/checkout
    private List<Long> cart_item_ids;   // 주문할 장바구니 항목, 1개 이상
}
