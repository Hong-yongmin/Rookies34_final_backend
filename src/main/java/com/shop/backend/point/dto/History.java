package com.shop.backend.point.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class History {
    private String type;
    private int amount;
    private String reason;
    private Date created_at;
}
