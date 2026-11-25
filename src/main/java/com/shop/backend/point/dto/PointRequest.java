package com.shop.backend.point.dto;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class PointRequest {
    private String type;
    private Integer page;
}
