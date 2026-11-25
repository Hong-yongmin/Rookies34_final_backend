package com.shop.backend.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerCancelRequest { // POST /api/seller/order-items/{orderItemId}/cancellation
    private String reason;  // OUT_OF_STOCK / ETC
}
