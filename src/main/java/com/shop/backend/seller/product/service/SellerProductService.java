package com.shop.backend.seller.product.service;

import com.shop.backend.product.dto.ProductDetailResponse;
import com.shop.backend.product.repository.ProductRepository;
import com.shop.backend.seller.product.dto.SellerProductRequest;
import com.shop.backend.seller.product.dto.SellerProductResponse;
import com.shop.backend.seller.product.dto.SellerProductUpdateRequest;
import com.shop.backend.seller.product.repository.SellerProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerProductService {
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
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
    @Transactional
    public void register(SellerProductRequest req, String sellerId, String contextPath) {
        // 썸네일 저장 → 경로
        String thumbnailUrl = saveFile(req.getThumbnail(), contextPath);

        // 상품 insert → 생성된 product_id
        Long productId = sellerProductRepository.insertProduct(req, sellerId, thumbnailUrl);
        if (productId == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "상품을 저장할 수 없습니다.");
        }

        // 상세 이미지 저장 → product_image
        List<String> detailPaths = saveDetailImages(req.getDetail_images(), contextPath);
        if (!detailPaths.isEmpty()) {
            sellerProductRepository.insertImages(productId, detailPaths);
        }
    }

    // 상품 수정 (S-02)
    @Transactional
    public ProductDetailResponse update(Long productId, SellerProductUpdateRequest req, String sellerId,
                                        MultipartFile thumbnail, List<MultipartFile> detailImages, String contextPath) {
        if (!sellerProductRepository.lockEditableProduct(productId, sellerId)) {
            throw productNotFound();
        }
        validateUpdate(req);
        String thumbnailUrl = saveFile(thumbnail, contextPath);
        sellerProductRepository.updateProduct(productId, sellerId, req, thumbnailUrl);

        // 상세 이미지가 새로 오면 교체
        List<String> detailPaths = saveDetailImages(detailImages, contextPath);
        if (!detailPaths.isEmpty()) {
            sellerProductRepository.deleteImages(productId);
            sellerProductRepository.insertImages(productId, detailPaths);
        }
        return getDetail(productId, sellerId);
    }

    private void validateUpdate(SellerProductUpdateRequest req) {
        if ((req.getName() != null && req.getName().isBlank())
                || (req.getCategory_id() != null && req.getCategory_id() <= 0)
                || (req.getPrice() != null && req.getPrice() < 0)
                || (req.getStock() != null && req.getStock() < 0)
                || (req.getDiscount_rate() != null && (req.getDiscount_rate() < 0 || req.getDiscount_rate() > 100))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품 수정 값을 확인해 주세요.");
        }
    }

    // 상세 이미지 파일들 저장 → 경로 리스트
    private List<String> saveDetailImages(List<MultipartFile> files, String contextPath) {
        List<String> paths = new ArrayList<>();
        if (files != null) {
            for (MultipartFile f : files) {
                String path = saveFile(f, contextPath);
                if (path != null) paths.add(path);
            }
        }
        return paths;
    }

    private String saveFile(MultipartFile file, String contextPath) {
        if (file == null || file.isEmpty()) {
            return null;    // 파일 없으면 null
        }
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (extension == null || !IMAGE_EXTENSIONS.contains(extension.toLowerCase(Locale.ROOT))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JPG, PNG, GIF, WebP 이미지만 업로드할 수 있습니다.");
        }
        String filename = UUID.randomUUID() + "." + extension.toLowerCase(Locale.ROOT);
        Path target = Path.of(productUploadDir).toAbsolutePath().normalize().resolve(filename);
        try {
            Files.createDirectories(target.getParent());
            file.transferTo(target);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) {
                        deleteUpload(target);
                    }
                }
            });
            return contextPath + "/uploads/products/" + filename;
        } catch (IOException e) {
            deleteUpload(target);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "상품 이미지를 저장할 수 없습니다.", e);
        }
    }

    private void deleteUpload(Path target) {
        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.warn("임시 상품 이미지 삭제 실패: {}", target, e);
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
