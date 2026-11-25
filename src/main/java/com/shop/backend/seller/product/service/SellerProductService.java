package com.shop.backend.seller.product.service;

import com.shop.backend.product.entity.Product;
import com.shop.backend.product.repository.ProductRepository;
import com.shop.backend.seller.product.dto.SellerProductRequest;
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
    private final ProductRepository productRepository;

    // 상품 등록 (S-02)
    public void register(SellerProductRequest req, String sellerId) {
        Product p = new Product();
        p.setName(req.getName());
        p.setCategory_id(req.getCategory_id());
        p.setPrice(req.getPrice());
        p.setDiscount_rate(req.getDiscount_rate() != null ? req.getDiscount_rate() : 0);
        p.setStock(req.getStock());
        p.setStorage_type(req.getStorage_type());
        p.setOrigin(req.getOrigin());
        p.setDescription(req.getDescription());

        p.setSeller_id(sellerId);     // 토큰에서 온 login_id
        p.setStatus("PENDING");       // 등록 시 검증 대기

        // 썸네일 저장
        p.setThumbnail(saveFile(req.getThumbnail()));

        // 상세 이미지 저장
        List<String> detailPaths = new ArrayList<>();
        if (req.getDetail_images() != null) {
            for (MultipartFile f : req.getDetail_images()) {
                String path = saveFile(f);
                if (path != null) detailPaths.add(path);
            }
        }
        p.setDetail_images(detailPaths);

        productRepository.save(p);
    }

    // 상품 수정 (S-02)
    public void update(Long productId, SellerProductRequest req, String sellerId) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음: " + productId));

        // 본인 상품 확인 생략 → 타 판매자 상품 수정

        p.setName(req.getName());
        p.setCategory_id(req.getCategory_id());
        p.setPrice(req.getPrice());
        p.setDiscount_rate(req.getDiscount_rate() != null ? req.getDiscount_rate() : 0);
        p.setStock(req.getStock());
        p.setStorage_type(req.getStorage_type());
        p.setOrigin(req.getOrigin());
        p.setDescription(req.getDescription());

        // 썸네일 저장
        p.setThumbnail(saveFile(req.getThumbnail()));

        // 상세 이미지들 저장
        List<String> detailPaths = new ArrayList<>();
        if (req.getDetail_images() != null) {
            for (MultipartFile f : req.getDetail_images()) {
                String path = saveFile(f);
                if (path != null) detailPaths.add(path);
            }
        }
        p.setDetail_images(detailPaths);

        productRepository.save(p);
    }

    private String saveFile(MultipartFile file){
        if (file == null || file.isEmpty()){
            return null;    // 파일 없으면 null
        }
        try{
            String uploadDir = "uploads/products/";
            Files.createDirectories(Paths.get(uploadDir));  // 폴더 없으면 생성

            // 확장자/타입 검증 없음
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path target = Paths.get(uploadDir, filename);
            file.transferTo(target.toAbsolutePath());   // 실제 파일 저장

            return "/" + uploadDir + filename;  // DB에 저장할 경로
        } catch (IOException e){
            throw new RuntimeException("파일 저장 실패: " + e.getMessage(), e);
        }
    }
}
