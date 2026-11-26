
package com.shop.backend.support.post.controller;

import com.shop.backend.support.post.dto.PostResponseDto;
import com.shop.backend.support.post.dto.NoticeListResponseDto;
import com.shop.backend.support.post.dto.FaqListResponseDto;
import com.shop.backend.support.post.entity.FaqCategory;
import com.shop.backend.support.post.service.PostService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 공지사항 목록 조회
    @GetMapping("/api/notices")
    public NoticeListResponseDto getNotices(
            @RequestParam(defaultValue = "0") int page
    ) {
        return postService.getNotices(page);
    }

    // 공지사항 상세 조회
    @GetMapping("/api/notices/{noticeId}")
    public PostResponseDto getNoticeDetail(
            @PathVariable Integer noticeId
    ) {
        return postService.getNoticeDetail(noticeId);
    }

    // FAQ 목록 조회
    @GetMapping("/api/faqs")
    public FaqListResponseDto getFaqs(
            @RequestParam(required = false) FaqCategory category,
            @RequestParam(defaultValue = "0") int page
    ) {
        return postService.getFaqs(category, page);
    }
}
