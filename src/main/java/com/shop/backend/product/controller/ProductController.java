package com.shop.backend.product.controller;

import com.shop.backend.product.dto.ListType;
import com.shop.backend.product.dto.ProductDetailResponse;
import com.shop.backend.product.dto.ProductSort;
import com.shop.backend.product.dto.ProductSummaryResponse;
import com.shop.backend.product.entity.Product;
import com.shop.backend.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // 상품 목록 조회 (U-20)
    // GET /api/products?list_type=CATEGORY&category_id=1&sort=PRICE_ASC&page=1
    @GetMapping
    public Map<String, Object> list(
            @RequestParam(name = "list_type") ListType listType,
            @RequestParam(name = "category_id", required = false) Long categoryId,
            @RequestParam(required = false) ProductSort sort,
            @RequestParam(defaultValue = "1") int page) {

        Page<Product> result = service.getList(listType, categoryId, sort, page);
        List<ProductSummaryResponse> products =
                result.map(ProductSummaryResponse::new).getContent();

        return Map.of(
                "code", "ok",
                "num", result.getTotalElements(),
                "products", products
        );
    }

    // 상품 검색 (U-21)
    // GET /api/products/search?keyword=우유&sort=RECOMMEND&page=1
    @GetMapping("/search")
    public Map<String, Object> search(
            @RequestParam String keyword,
            @RequestParam(required = false) ProductSort sort,
            @RequestParam(defaultValue = "1") int page) {

        Page<Product> result = service.search(keyword, sort, page);
        List<ProductSummaryResponse> products =
                result.map(ProductSummaryResponse::new).getContent();

        return Map.of(
                "code", "ok",
                "num", result.getTotalElements(),
                "products", products
        );
    }

    // 상품 상세 조회 (U-22)
    // GET /api/products/{productId}
    @GetMapping("/{productId}")
    public Map<String, Object> detail(@PathVariable Long productId) {
        ProductDetailResponse product = service.getDetail(productId);
        return Map.of("code", "ok", "product", product);
    }
}
