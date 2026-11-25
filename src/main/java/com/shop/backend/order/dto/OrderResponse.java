package com.shop.backend.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderResponse { // 배송 정보 입력, 판매자 주문 취소 등 code만 반환하는 응답
    private String code;
}
