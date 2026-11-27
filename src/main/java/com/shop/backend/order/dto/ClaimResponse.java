package com.shop.backend.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ClaimResponse {
    private String code;
    private Integer num;
    private List<Claim> claims;
}
