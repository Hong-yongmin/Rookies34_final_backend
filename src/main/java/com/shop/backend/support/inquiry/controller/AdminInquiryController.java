
package com.shop.backend.support.inquiry.controller;

import com.shop.backend.support.inquiry.dto.InquiryAnswerRequestDto;
import com.shop.backend.support.inquiry.dto.AdminInquiryListResponseDto;
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
@RequestMapping("/api/admin/inquiries")
public class AdminInquiryController {

    private final InquiryService inquiryService;
    private final AdminChecker adminChecker;

    @GetMapping
    public AdminInquiryListResponseDto getInquiries(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String userId,
            @RequestParam(name = "page", defaultValue = "1") int page
    ) {
        requireAdmin(userId);
        if (page < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page must be at least 1");
        }
        return inquiryService.getAdminInquiries(page - 1);
    }

    // 관리자 문의 답변 등록
    @PostMapping("/{inquiryId}/answer")
    public InquiryCodeResponseDto createAnswer(

            @RequestAttribute(
                    value = JwtAuthenticationFilter.MEMBER_ID,
                    required = false
            ) String userId,

            @PathVariable Integer inquiryId,

            @RequestBody InquiryAnswerRequestDto request
    ) {
        requireAdmin(userId);
        return inquiryService.createAnswer(inquiryId, request);
    }

    private void requireAdmin(String userId) {
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

    }
}
