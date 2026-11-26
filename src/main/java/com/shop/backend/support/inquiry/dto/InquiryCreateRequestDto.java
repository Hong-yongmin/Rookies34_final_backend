
package com.shop.backend.support.inquiry.dto;

import lombok.Getter;
import lombok.Setter;

import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class InquiryCreateRequestDto {

    private String category;

    private String order_id;

    private String title;

    private String content;

    private MultipartFile attachment;
}
