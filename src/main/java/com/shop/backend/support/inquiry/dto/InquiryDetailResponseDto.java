
package com.shop.backend.support.inquiry.dto;

import com.shop.backend.support.inquiry.entity.Inquiry;

import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class InquiryDetailResponseDto {

    private String code;
    private Integer inquiry_id;
    private String category;
    private String order_id;
    private String title;
    private String content;
    private String attachment;
    private String answer;
    private String created_at;

    public InquiryDetailResponseDto(Inquiry inquiry) {

        this.code = "ok";

        this.inquiry_id = inquiry.getInquiry_id();

        this.category = inquiry.getCategory().name();

        this.order_id = inquiry.getOrder_id() == null
                ? null
                : inquiry.getOrder_id().toString();

        this.title = inquiry.getTitle();

        this.content = inquiry.getContent();

        // 첨부파일이 있으면 다운로드 API 주소 반환
        this.attachment = inquiry.getAttachment_url() == null
                || inquiry.getAttachment_url().isBlank()
                ? null
                : "/api/inquiries/"
                + inquiry.getInquiry_id()
                + "/attachment";

        this.answer = inquiry.getAnswer();

        this.created_at = inquiry.getCreated_at()
                .format(
                        DateTimeFormatter.ofPattern(
                                "yyyy-MM-dd HH:mm:ss"
                        )
                );
    }
}
