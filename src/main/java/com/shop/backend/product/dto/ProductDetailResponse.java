package com.shop.backend.product.dto;

import com.shop.backend.product.entity.Product;
import lombok.Getter;
import java.util.List;

// 상품 상세 응답용
@Getter
public class ProductDetailResponse {

    private Long product_id;
    private String name;
    private Long category_id;
    private int price;
    private int discount_rate;
    private int stock;
    private String storage_type;
    private String origin;
    private String description;
    private String thumbnail;
    private List<String> detail_images;

    public ProductDetailResponse(Product p) {
        this.product_id = p.getId();
        this.name = p.getName();
        this.category_id = p.getCategory_id();
        this.price = p.getPrice();
        this.discount_rate = p.getDiscount_rate();
        this.stock = p.getStock();
        this.storage_type = p.getStorage_type() != null ? p.getStorage_type().name() : null;
        this.origin = p.getOrigin();
        this.description = p.getDescription();
        this.thumbnail = p.getThumbnail();
        this.detail_images = p.getDetail_images();
    }
}
