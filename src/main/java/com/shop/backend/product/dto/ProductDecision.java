package com.shop.backend.product.dto;

// A-05 관리자 처리 결정 — 각 decision이 product.status 값으로 매핑됨
public enum ProductDecision {
    APPROVE("APPROVED"),    // 승인
    REJECT("REJECTED"),     // 반려
    WITHDRAW("WITHDRAWN");  // 철회

    private final String status;

    ProductDecision(String status) {
        this.status = status;
    }

    // decision → DB status 문자열
    public String toStatus() {
        return status;
    }
}
