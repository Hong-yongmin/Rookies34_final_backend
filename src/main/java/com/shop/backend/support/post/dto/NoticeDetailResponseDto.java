package com.shop.backend.support.post.dto;

import com.shop.backend.support.post.entity.Post;

import java.time.LocalDateTime;

public record NoticeDetailResponseDto(
        String code,
        Integer notice_id,
        String title,
        String content,
        Boolean is_pinned,
        LocalDateTime created_at
) {
    public NoticeDetailResponseDto(Post post) {
        this("ok", post.getPost_id(), post.getTitle(), post.getContent(),
                post.getIs_pinned(), post.getCreated_at());
    }
}
