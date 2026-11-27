package com.shop.backend.seller.product.controller;

import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.SellerChecker;
import com.shop.backend.seller.product.dto.SellerProductEditResponse;
import com.shop.backend.seller.product.dto.SellerProductRequest;
import com.shop.backend.seller.product.dto.SellerProductResponse;
import com.shop.backend.seller.product.dto.SellerProductUpdateRequest;
import com.shop.backend.seller.product.service.SellerProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/products")
public class SellerProductController {
    private final SellerProductService sellerProductService;
    private final SellerChecker sellerChecker;

    // 내 상품 목록 조회 (S-11)
    @GetMapping
    public Map<String, Object> myProducts(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page) {

        if (!sellerChecker.isSeller(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }

        List<SellerProductResponse> products =
                sellerProductService.getMyProducts(id, status, keyword, page);

        return Map.of(
                "code", "ok",
                "num", products.size(),
                "products", products
        );
    }

    @GetMapping("/{productId}")
    public SellerProductEditResponse detail(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @PathVariable Long productId,
            HttpServletRequest request) {
        requireSeller(id);
        return editResponse(productId, id, request.getContextPath());
    }

    // 상품 등록 (S-02)
    @PostMapping
    public Map<String, Object> register(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @ModelAttribute SellerProductRequest req,
            HttpServletRequest request) {

        // 판매자 권한 확인
        if (!sellerChecker.isSeller(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }

        // 등록 (토큰의 id를 seller_id로)
        sellerProductService.register(req, id, request.getContextPath());
        return Map.of("code", "ok");
    }

    // 상품 수정 (S-02)
    @PutMapping(path = "/{productId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public SellerProductEditResponse updateForm(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @PathVariable Long productId,
            @ModelAttribute SellerProductUpdateRequest req,
            @RequestParam(name = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestParam(name = "detail_images", required = false) List<MultipartFile> detailImages,
            HttpServletRequest request) {
        requireSeller(id);
        String contextPath = request.getContextPath();
        var detail = sellerProductService.update(productId, req, id, thumbnail, detailImages, contextPath);
        return new SellerProductEditResponse("ok", new SellerProductEditResponse.Product(detail, contextPath));
    }

    @PutMapping(path = "/{productId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SellerProductEditResponse updateJson(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @PathVariable Long productId,
            @RequestBody SellerProductUpdateRequest req,
            HttpServletRequest request) {
        requireSeller(id);
        String contextPath = request.getContextPath();
        var detail = sellerProductService.update(productId, req, id, null, null, contextPath);
        return new SellerProductEditResponse("ok", new SellerProductEditResponse.Product(detail, contextPath));
    }

    private SellerProductEditResponse editResponse(Long productId, String sellerId, String contextPath) {
        var detail = sellerProductService.getDetail(productId, sellerId);
        return new SellerProductEditResponse("ok", new SellerProductEditResponse.Product(detail, contextPath));
    }

    private void requireSeller(String id) {
        if (!sellerChecker.isSeller(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }
    }

    // 등록 상품 삭제 (새 API)
    @DeleteMapping("/{productId}")
    public Map<String, Object> delete(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @PathVariable Long productId
    ){
        // 판매자 권한 확인
        if (!sellerChecker.isSeller(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }

        sellerProductService.deleteProduct(productId, id);
        return Map.of("code", "ok");
    }
}
