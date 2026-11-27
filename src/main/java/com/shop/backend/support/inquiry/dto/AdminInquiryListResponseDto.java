package com.shop.backend.support.inquiry.dto;

import com.shop.backend.support.inquiry.entity.Inquiry;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public record AdminInquiryListResponseDto(String code, long num, List<Item> inquiries) {

    @Getter
    public static class Item extends InquiryListItemDto {

        private final String user_id;
        private final Integer order_id;
        private final String content;
        private final String answer;
        private final LocalDateTime answered_at;
        private final boolean has_attachment;

        public Item(Inquiry inquiry) {
            super(inquiry);
            this.user_id = inquiry.getUser_id();
            this.order_id = inquiry.getOrder_id();
            this.content = inquiry.getContent();
            this.answer = inquiry.getAnswer();
            this.answered_at = inquiry.getAnswered_at();
            String attachment = inquiry.getAttachment_url();
            this.has_attachment = attachment != null && !attachment.isBlank();
        }
    }
}
