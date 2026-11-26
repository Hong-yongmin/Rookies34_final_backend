
package com.shop.backend.support.inquiry.dto;

import com.shop.backend.support.inquiry.entity.InquiryCategory;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InquiryResponseDto {

    private Integer inquiry_id;

    private InquiryCategory category;

    private Integer order_id;

    private String title;

    private String content;

    private String attachment_url;

    private String answer;

    private LocalDateTime answered_at;

    private LocalDateTime created_at;

    public InquiryResponseDto(
            Integer inquiry_id,
            InquiryCategory category,
            Integer order_id,
            String title,
            String content,
            String attachment_url,
            String answer,
            LocalDateTime answered_at,
            LocalDateTime created_at
    ) {
        this.inquiry_id = inquiry_id;
        this.category = category;
        this.order_id = order_id;
        this.title = title;
        this.content = content;
        this.attachment_url = attachment_url;
        this.answer = answer;
        this.answered_at = answered_at;
        this.created_at = created_at;
    }
}
