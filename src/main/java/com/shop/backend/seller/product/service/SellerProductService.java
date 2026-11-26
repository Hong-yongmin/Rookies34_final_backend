package com.shop.backend.seller.product.service;

import com.shop.backend.seller.product.dto.SellerProductRequest;
import com.shop.backend.seller.product.dto.SellerProductResponse;
import com.shop.backend.seller.product.repository.SellerProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SellerProductService {
    private final SellerProductRepository sellerProductRepository;

    // 내 상품 목록 조회 (S-11)
    public List<SellerProductResponse> getMyProducts(String sellerId, String status, String keyword, int page) {
        return sellerProductRepository.getMyProducts(sellerId, status, keyword, page);
    }

    // 상품 등록 (S-02)
    public void register(SellerProductRequest req, String sellerId) {
        // 썸네일 저장 → 경로
        String thumbnailUrl = saveFile(req.getThumbnail());

        // 상품 insert → 생성된 product_id
        Long productId = sellerProductRepository.insertProduct(req, sellerId, thumbnailUrl);

        // 상세 이미지 저장 → product_image
        List<String> detailPaths = saveDetailImages(req.getDetail_images());
        if (productId != null && !detailPaths.isEmpty()) {
            sellerProductRepository.insertImages(productId, detailPaths);
        }
    }

    // 상품 수정 (S-02) — 본인 상품 확인 생략(수평권한 진단 포인트)
    public void update(Long productId, SellerProductRequest req, String sellerId) {
        String thumbnailUrl = saveFile(req.getThumbnail());   // 새 파일 없으면 null
        sellerProductRepository.updateProduct(productId, req, thumbnailUrl);

        // 상세 이미지가 새로 오면 교체
        List<String> detailPaths = saveDetailImages(req.getDetail_images());
        if (!detailPaths.isEmpty()) {
            sellerProductRepository.deleteImages(productId);
            sellerProductRepository.insertImages(productId, detailPaths);
        }
    }

    // 상세 이미지 파일들 저장 → 경로 리스트
    private List<String> saveDetailImages(List<MultipartFile> files) {
        List<String> paths = new ArrayList<>();
        if (files != null) {
            for (MultipartFile f : files) {
                String path = saveFile(f);
                if (path != null) paths.add(path);
            }
        }
        return paths;
    }

    private String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;    // 파일 없으면 null
        }
        try {
            String uploadDir = "uploads/products/";
            Files.createDirectories(Paths.get(uploadDir));  // 폴더 없으면 생성

            // 확장자/타입 검증 없음
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path target = Paths.get(uploadDir, filename);
            file.transferTo(target.toAbsolutePath());   // 실제 파일 저장

            return "/" + uploadDir + filename;  // DB에 저장할 경로
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + e.getMessage(), e);
        }
    }
}
