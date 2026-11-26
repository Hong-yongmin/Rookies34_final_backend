
package com.shop.backend.support.post.dto;

import com.shop.backend.support.post.entity.Post;
import lombok.Getter;

@Getter
public class FaqListItemDto {

    private Integer faq_id;
    private String faq_category;
    private String title;
    private String content;

    public FaqListItemDto(Post post) {

        this.faq_id = post.getPost_id();
        this.faq_category = post.getFaq_category().name();
        this.title = post.getTitle();
        this.content = post.getContent();
    }
}
