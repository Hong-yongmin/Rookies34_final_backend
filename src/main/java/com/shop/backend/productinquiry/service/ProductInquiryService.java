package com.shop.backend.productinquiry.service;

import com.shop.backend.productinquiry.dto.MyInquiryDetailResponse;
import com.shop.backend.productinquiry.dto.MyInquiryResponse;
import com.shop.backend.productinquiry.dto.ProductInquiryRequest;
import com.shop.backend.productinquiry.dto.ProductInquiryResponse;
import com.shop.backend.productinquiry.repository.ProductInquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductInquiryService {

    private final ProductInquiryRepository repository;

    // 상품 문의 작성 (U-25)
    public void create(Long productId, ProductInquiryRequest req, String writer) {
        Boolean sec = req.getIs_secret();
        boolean isSecret = (sec != null) && sec;
        repository.insert(productId, writer, req.getTitle(), req.getContent(), isSecret);   // writer = 토큰 id
    }

    // 상품 문의 목록 조회 (U-26)
    public List<ProductInquiryResponse> getList(Long productId, int page) {
        return repository.getByProduct(productId, page);
    }

    // 내 문의 내역 조회 (U-53)
    public List<MyInquiryResponse> getMyList(String userId, int page) {
        return repository.getMyInquiries(userId, page);
    }

    // 내 문의 상세 조회 (U-53 상세)
    public MyInquiryDetailResponse getDetail(Long inquiryId) {
        MyInquiryDetailResponse detail = repository.getMyInquiryDetail(inquiryId);
        if (detail == null) {
            throw new IllegalArgumentException("문의 없음: " + inquiryId);
        }
        return detail;
    }
}
