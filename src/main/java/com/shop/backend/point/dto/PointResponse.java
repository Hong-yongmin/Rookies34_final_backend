package com.shop.backend.point.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class PointResponse {
    private String code;
    private int balance;
    private int num;
    private List<History> histories;
}
