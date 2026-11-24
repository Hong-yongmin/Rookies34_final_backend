package com.shop.backend.point.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class History {
    private String type;
    private int amount;
    private String reason;
    private Date created_at;
}
