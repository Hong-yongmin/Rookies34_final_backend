package com.shop.backend.member.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SellerInfoResponse {
    private String code;

    @JsonUnwrapped   // sellerInfo의 필드를 최상위로 펼쳐서 응답 (code와 같은 depth)
    private SellerInfo sellerInfo;
}
