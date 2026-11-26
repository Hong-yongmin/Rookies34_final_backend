
package com.shop.backend.support.post.dto;

import com.shop.backend.support.post.entity.BoardType;
import com.shop.backend.support.post.entity.FaqCategory;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDto {

    private Integer post_id;

    private BoardType board_type;

    private FaqCategory faq_category;

    private String title;

    private String content;

    private Boolean is_pinned;

    private LocalDateTime created_at;

    public PostResponseDto(
            Integer post_id,
            BoardType board_type,
            FaqCategory faq_category,
            String title,
            String content,
            Boolean is_pinned,
            LocalDateTime created_at
    ) {
        this.post_id = post_id;
        this.board_type = board_type;
        this.faq_category = faq_category;
        this.title = title;
        this.content = content;
        this.is_pinned = is_pinned;
        this.created_at = created_at;
    }
}
