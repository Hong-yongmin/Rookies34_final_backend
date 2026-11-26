
package com.shop.backend.support.inquiry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class InquiryListResponseDto {

    private String code;
    private Long num;
    private List<InquiryListItemDto> inquiries;
}
