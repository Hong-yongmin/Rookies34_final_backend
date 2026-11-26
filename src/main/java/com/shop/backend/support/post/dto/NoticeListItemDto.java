
package com.shop.backend.support.post.dto;

import com.shop.backend.support.post.entity.Post;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class NoticeListItemDto {

    private Integer notice_id;
    private String title;
    private Boolean is_pinned;
    private String created_at;

    public NoticeListItemDto(Post post) {

        this.notice_id = post.getPost_id();
        this.title = post.getTitle();
        this.is_pinned = post.getIs_pinned();

        this.created_at = post.getCreated_at()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
