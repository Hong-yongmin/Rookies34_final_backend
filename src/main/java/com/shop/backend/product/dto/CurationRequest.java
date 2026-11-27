package com.shop.backend.product.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// A-07 큐레이션 지정/해제 요청 — { "product_ids":[1,2,3], "curation":"BEST" }
// curation이 null/빈값이면 해제(NULL로)
@Getter
@Setter
public class CurationRequest {
    private List<Long> product_ids;
    private String curation;   // BEST / SALE / NEW_ARRIVAL / BENEFIT, null이면 해제
}
