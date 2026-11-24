package com.shop.backend.point.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class PointRequest {
    private String type;
    private int page;
}
