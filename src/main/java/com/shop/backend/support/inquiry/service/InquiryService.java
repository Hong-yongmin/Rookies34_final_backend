
package com.shop.backend.support.inquiry.service;

import com.shop.backend.support.inquiry.dto.InquiryAnswerRequestDto;
import com.shop.backend.support.inquiry.dto.InquiryCreateRequestDto;
import com.shop.backend.support.inquiry.dto.InquiryResponseDto;
import com.shop.backend.support.inquiry.dto.InquiryListItemDto;
import com.shop.backend.support.inquiry.dto.InquiryListResponseDto;
import com.shop.backend.support.inquiry.dto.InquiryDetailResponseDto;
import com.shop.backend.support.inquiry.dto.InquiryCodeResponseDto;

import com.shop.backend.support.inquiry.entity.Inquiry;
import com.shop.backend.support.inquiry.entity.InquiryCategory;

import com.shop.backend.support.inquiry.repository.InquiryRepository;
import com.shop.backend.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryFileService inquiryFileService;
    private final OrderRepository orderRepository;

    // 1:1 문의 작성
    @Transactional
    public InquiryResponseDto createInquiry(
            InquiryCreateRequestDto request,
            String userId
    ) {
        if (request.getCategory() == null
                || request.getTitle() == null || request.getTitle().isBlank()
                || request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("필수 입력값을 확인해 주세요.");
        }

        if (request.getTitle().length() > 50) {
            throw new IllegalArgumentException("제목은 최대 50자까지 입력할 수 있습니다.");
        }

        if (request.getContent().length() > 2000) {
            throw new IllegalArgumentException("내용은 최대 2000자까지 입력할 수 있습니다.");
        }

        // 문의 유형 검사
        InquiryCategory category;

        try {
            category = InquiryCategory.valueOf(request.getCategory());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("올바르지 않은 문의 유형입니다.");
        }

        // 주문 ID 변환 및 소유권 확인
        Integer orderId = null;

        if (request.getOrder_id() != null && !request.getOrder_id().isBlank()) {
            try {
                orderId = Integer.valueOf(request.getOrder_id());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("올바르지 않은 주문 번호입니다.");
            }

            if (!orderRepository.isOrderOwnedByUser(userId, orderId)) {
                throw new IllegalArgumentException("본인의 주문만 문의할 수 있습니다.");
            }
        }

        // 첨부파일 저장
        String attachmentUrl = null;

        if (request.getAttachment() != null
                && !request.getAttachment().isEmpty()) {

            String savedFilename =
                    inquiryFileService.saveFile(request.getAttachment());

            attachmentUrl = savedFilename;
        }

        // 문의 Entity 생성
        Inquiry inquiry = new Inquiry();

        inquiry.setUser_id(userId);
        inquiry.setCategory(category);
        inquiry.setOrder_id(orderId);
        inquiry.setTitle(request.getTitle());
        inquiry.setContent(request.getContent());
        inquiry.setAttachment_url(attachmentUrl);
        inquiry.setCreated_at(LocalDateTime.now());

        Inquiry savedInquiry = inquiryRepository.save(inquiry);

        return toResponseDto(savedInquiry);
    }

    // 본인의 1:1 문의 목록 조회 (페이지네이션)
    @Transactional(readOnly = true)
    public InquiryListResponseDto getInquiries(
            String userId,
            int page
    ) {
        // 페이지 번호 검증
        if (page < 0) {
            throw new IllegalArgumentException(
                    "페이지 번호는 0 이상이어야 합니다."
            );
        }

        // 페이지당 조회 개수
        int pageSize = 10;

        // 페이지 정보 생성
        Pageable pageable = PageRequest.of(page, pageSize);

        // 해당 페이지의 문의 목록 조회
        List<Inquiry> inquiries =
                inquiryRepository.findInquiriesByUserId(userId, pageable);

        // 전체 문의 개수 조회
        long totalCount =
                inquiryRepository.countByUser_id(userId);

        // 목록 DTO 변환
        List<InquiryListItemDto> inquiryList = inquiries.stream()
                .map(InquiryListItemDto::new)
                .toList();

        // 최종 응답 반환
        return new InquiryListResponseDto(
                "ok",
                totalCount,
                inquiryList
        );
    }

    // 1:1 문의 상세 조회
    @Transactional(readOnly = true)
    public InquiryDetailResponseDto getInquiryDetail(
            Integer inquiryId,
            String userId
    ) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 문의입니다.")
                );

        if (!inquiry.getUser_id().equals(userId)) {
            throw new IllegalArgumentException(
                    "본인의 문의만 조회할 수 있습니다."
            );
        }

        return new InquiryDetailResponseDto(inquiry);
    }

    // 1:1 문의 첨부파일 다운로드 경로 조회
    @Transactional(readOnly = true)
    public Path getInquiryAttachment(Integer inquiryId, String userId) {

        // 문의 존재 여부 확인
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 문의입니다.")
                );

        // 문의 작성자 확인
        if (!inquiry.getUser_id().equals(userId)) {
            throw new IllegalArgumentException(
                    "본인의 문의 첨부파일만 다운로드할 수 있습니다."
            );
        }

        // 첨부파일 존재 여부 확인
        String filename = inquiry.getAttachment_url();

        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException(
                    "첨부파일이 존재하지 않습니다."
            );
        }

        return inquiryFileService.getFilePath(filename);
    }


    // 관리자 답변 등록
    @Transactional
    public InquiryCodeResponseDto createAnswer(
            Integer inquiryId,
            InquiryAnswerRequestDto request
    ) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 문의입니다.")
                );

        if (request == null
                || request.getAnswer() == null
                || request.getAnswer().isBlank()) {
            throw new IllegalArgumentException(
                    "답변 내용을 입력해 주세요."
            );
        }

        if (request.getAnswer().length() > 2000) {
            throw new IllegalArgumentException(
                    "답변은 최대 2000자까지 입력할 수 있습니다."
            );
        }

        inquiry.setAnswer(request.getAnswer());
        inquiry.setAnswered_at(LocalDateTime.now());

        return new InquiryCodeResponseDto("ok");
    }


    // Entity를 Response DTO로 변환
    private InquiryResponseDto toResponseDto(Inquiry inquiry) {

        return new InquiryResponseDto(
                inquiry.getInquiry_id(),
                inquiry.getCategory(),
                inquiry.getOrder_id(),
                inquiry.getTitle(),
                inquiry.getContent(),
                inquiry.getAttachment_url(),
                inquiry.getAnswer(),
                inquiry.getAnswered_at(),
                inquiry.getCreated_at()
        );
    }
}
