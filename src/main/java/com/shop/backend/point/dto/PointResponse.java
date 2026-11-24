package com.shop.backend.point.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class PointResponse {
    private String code;
    private int balance;
    private int num;
    private List<History>
}
