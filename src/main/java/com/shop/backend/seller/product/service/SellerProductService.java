package com.shop.backend.seller.product.service;

import com.shop.backend.product.dto.ProductDetailResponse;
import com.shop.backend.product.repository.ProductRepository;
import com.shop.backend.seller.product.dto.SellerProductRequest;
import com.shop.backend.seller.product.dto.SellerProductResponse;
import com.shop.backend.seller.product.repository.SellerProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SellerProductService {
    private final SellerProductRepository sellerProductRepository;
    private final ProductRepository productRepository;

    // 상품 이미지 저장 절대경로 (properties 없으면 기본값 사용)
    @Value("${file.product-upload-dir:/var/shop/uploads/products}")
    private String productUploadDir;

    // 내 상품 목록 조회 (S-11)
    public List<SellerProductResponse> getMyProducts(String sellerId, String status, String keyword, int page) {
        return sellerProductRepository.getMyProducts(sellerId, status, keyword, page);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getDetail(Long productId, String sellerId) {
        ProductDetailResponse detail = productRepository.getSellerDetail(productId, sellerId);
        if (detail == null) {
            throw productNotFound();
        }
        detail.setDetail_images(productRepository.getDetailImages(productId));
        return detail;
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

    // 상품 수정 (S-02)
    @Transactional
    public void update(Long productId, SellerProductRequest req, String sellerId) {
        if (!sellerProductRepository.lockEditableProduct(productId, sellerId)) {
            throw productNotFound();
        }
        String thumbnailUrl = saveFile(req.getThumbnail());   // 새 파일 없으면 null
        sellerProductRepository.updateProduct(productId, sellerId, req, thumbnailUrl);

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
            Files.createDirectories(Paths.get(productUploadDir));  // 폴더 없으면 생성

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            file.transferTo(Paths.get(productUploadDir, filename));   // 실제 파일 저장(절대경로)

            return "/uploads/products/" + filename;  // DB엔 URL만 (물리경로 X) → WebConfig가 서빙
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + e.getMessage(), e);
        }
    }

    // 상품 삭제
    public void deleteProduct(Long productId, String sellerId) {
        if (!sellerProductRepository.withdrawProduct(productId, sellerId)) {
            throw productNotFound();
        }
    }

    private ResponseStatusException productNotFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다.");
    }
}
