
package com.shop.backend.support.post.controller;

import com.shop.backend.support.post.dto.PostCodeResponseDto;
import com.shop.backend.support.post.dto.PostCreateRequestDto;
import com.shop.backend.support.post.service.PostService;

import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.AdminChecker;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/posts")
public class AdminPostController {

    private final PostService postService;
    private final AdminChecker adminChecker;

    // 관리자 게시글 작성
    @PostMapping
    public PostCodeResponseDto createPost(

            @RequestAttribute(
                    value = JwtAuthenticationFilter.MEMBER_ID,
                    required = false
            ) String userId,

            @RequestBody PostCreateRequestDto request
    ) {

        // 로그인 여부 확인
        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "로그인이 필요합니다."
            );
        }

        // 관리자 권한 확인
        if (!Boolean.TRUE.equals(adminChecker.isAdmin(userId))) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "관리자 권한이 필요합니다."
            );
        }

        // 게시글 작성
        postService.createPost(request);

        return new PostCodeResponseDto("ok");
    }
}
