
package com.shop.backend.support.post.dto;

import com.shop.backend.support.post.entity.BoardType;
import com.shop.backend.support.post.entity.FaqCategory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateRequestDto {

    private BoardType board_type;

    private FaqCategory faq_category;

    private String title;

    private String content;

    private Boolean is_pinned;
}
