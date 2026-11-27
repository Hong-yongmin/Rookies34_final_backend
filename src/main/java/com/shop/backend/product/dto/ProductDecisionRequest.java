package com.shop.backend.product.dto;

import lombok.Getter;
import lombok.Setter;

// A-05 요청 바디 — { "decision": "APPROVE|REJECT|WITHDRAW" }
@Getter
@Setter
public class ProductDecisionRequest {
    private ProductDecision decision;
}
