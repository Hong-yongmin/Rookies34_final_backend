
package com.shop.backend.support.post.controller;

import com.shop.backend.support.post.dto.NoticeDetailResponseDto;
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
            @RequestParam(name = "page", defaultValue = "1") int page
    ) {
        return postService.getNotices(page > 0 ? page - 1 : page);
    }

    // 공지사항 상세 조회
    @GetMapping("/api/notices/{noticeId}")
    public NoticeDetailResponseDto getNoticeDetail(
            @PathVariable("noticeId") Integer noticeId
    ) {
        return postService.getNoticeDetail(noticeId);
    }

    // FAQ 목록 조회
    @GetMapping("/api/faqs")
    public FaqListResponseDto getFaqs(
            @RequestParam(name = "category", required = false) FaqCategory category,
            @RequestParam(name = "page", defaultValue = "1") int page
    ) {
        return postService.getFaqs(category, page > 0 ? page - 1 : page);
    }
}
