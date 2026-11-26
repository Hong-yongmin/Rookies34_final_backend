package com.shop.backend.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FindIdResponse {
    private String code;
    private String id;
}
