
package com.shop.backend.support.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FaqListResponseDto {

    private String code;
    private Long num;
    private List<FaqListItemDto> faqs;
}
