
package com.shop.backend.support.inquiry.controller;

import com.shop.backend.support.inquiry.dto.InquiryCodeResponseDto;
import com.shop.backend.support.inquiry.dto.InquiryCreateRequestDto;
import com.shop.backend.support.inquiry.dto.InquiryResponseDto;
import com.shop.backend.support.inquiry.dto.InquiryDetailResponseDto;
import com.shop.backend.support.inquiry.dto.InquiryListResponseDto;
import com.shop.backend.support.inquiry.service.InquiryService;
import com.shop.backend.support.inquiry.service.InquiryAttachmentLinkService;
import com.shop.backend.global.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.net.MalformedURLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;
    private final InquiryAttachmentLinkService attachmentLinkService;

    // 1:1 문의 작성
    @PostMapping(consumes = "multipart/form-data")
    public InquiryCodeResponseDto createInquiry(
            @RequestAttribute(
                    value = JwtAuthenticationFilter.MEMBER_ID,
                    required = false
            ) String userId,

            @ModelAttribute InquiryCreateRequestDto request
    ) {
        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "로그인이 필요합니다."
            );
        }

        inquiryService.createInquiry(request, userId);

        return new InquiryCodeResponseDto("ok");
    }

    // 1:1 문의 목록 조회
    @GetMapping
    public InquiryListResponseDto getInquiries(
            @RequestAttribute(
                    value = JwtAuthenticationFilter.MEMBER_ID,
                    required = false
            ) String userId,

            @RequestParam(defaultValue = "0") int page
    ) {
        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "로그인이 필요합니다."
            );
        }

        return inquiryService.getInquiries(userId, page);
    }

    // 1:1 문의 상세 조회
    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryDetailResponseDto> getInquiryDetail(
            @RequestAttribute(
                    value = JwtAuthenticationFilter.MEMBER_ID,
                    required = false
            ) String userId,

            @PathVariable Integer inquiryId
    ) {
        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "로그인이 필요합니다."
            );
        }

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(inquiryService.getInquiryDetail(inquiryId, userId));
    }

    // 1:1 문의 첨부파일 다운로드
    @GetMapping("/{inquiryId}/attachment")
    public ResponseEntity<Resource> downloadAttachment(
            @RequestAttribute(
                    value = JwtAuthenticationFilter.MEMBER_ID,
                    required = false
            ) String userId,

            @PathVariable("inquiryId") Integer inquiryId,
            @RequestParam(name = "download_token", required = false) String downloadToken
    ) throws MalformedURLException {

        String owner = userId == null ? attachmentLinkService.getOwner(inquiryId, downloadToken) : userId;
        Path filePath =
                inquiryService.getInquiryAttachment(inquiryId, owner);

        Resource resource = new UrlResource(filePath.toUri());

        String filename = filePath.getFileName().toString();

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(filename, StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(resource);
    }
}
