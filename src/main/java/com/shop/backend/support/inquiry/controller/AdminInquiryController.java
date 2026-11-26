
package com.shop.backend.support.inquiry.controller;

import com.shop.backend.support.inquiry.dto.InquiryAnswerRequestDto;
import com.shop.backend.support.inquiry.dto.InquiryCodeResponseDto;
import com.shop.backend.support.inquiry.service.InquiryService;
import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.AdminChecker;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class AdminInquiryController {

    private final InquiryService inquiryService;
    private final AdminChecker adminChecker;

    // 관리자 문의 답변 등록
    @PostMapping("/api/admin/inquiries/{inquiryId}/answer")
    public InquiryCodeResponseDto createAnswer(

            @RequestAttribute(
                    value = JwtAuthenticationFilter.MEMBER_ID,
                    required = false
            ) String userId,

            @PathVariable Integer inquiryId,

            @RequestBody InquiryAnswerRequestDto request
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

        // 답변 등록
        return inquiryService.createAnswer(inquiryId, request);
    }
}
