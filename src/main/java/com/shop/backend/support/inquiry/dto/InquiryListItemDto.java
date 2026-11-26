
package com.shop.backend.support.inquiry.dto;

import com.shop.backend.support.inquiry.entity.Inquiry;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class InquiryListItemDto {

    private Integer inquiry_id;
    private String category;
    private String title;
    private Boolean is_answered;
    private String created_at;

    public InquiryListItemDto(Inquiry inquiry) {

        this.inquiry_id = inquiry.getInquiry_id();
        this.category = inquiry.getCategory().name();
        this.title = inquiry.getTitle();

        this.is_answered =
                inquiry.getAnswer() != null
                        && !inquiry.getAnswer().isBlank();

        this.created_at = inquiry.getCreated_at()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
