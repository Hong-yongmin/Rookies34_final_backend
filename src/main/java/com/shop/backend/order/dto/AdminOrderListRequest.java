package com.shop.backend.order.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class AdminOrderListRequest { // GET /api/admin/orders
    private String date_from;
    private String date_to;
    private String status;      // 해당 status의 order_item이 있는 주문만 조회 (PAID / SHIPPING / COMPLETED / CANCELED)
    private String seller_id;
    private Integer page;
}
