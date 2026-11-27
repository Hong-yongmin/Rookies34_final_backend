package com.shop.backend.seller.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.shop.backend.product.dto.ProductDetailResponse;

import java.util.List;

public record SellerProductEditResponse(String code, @JsonUnwrapped Product fields) {

    @JsonProperty("product")
    public Product product() {
        return fields;
    }

    @JsonProperty("detail")
    public Product detail() {
        return fields;
    }

    public record Product(
            Long product_id, String name, Long category_id, int price, int discount_rate, int stock,
            String storage_type, String origin, String description, String thumbnail, List<String> detail_images
    ) {
        public Product(ProductDetailResponse detail, String contextPath) {
            this(detail.getProduct_id(), detail.getName(), detail.getCategory_id(), detail.getPrice(),
                    detail.getDiscount_rate(), detail.getStock(), detail.getStorage_type(), detail.getOrigin(),
                    detail.getDescription(), imageUrl(detail.getThumbnail(), contextPath),
                    detail.getDetail_images().stream().map(url -> imageUrl(url, contextPath)).toList());
        }

        @JsonProperty("thumbnail_url")
        public String thumbnailUrl() {
            return thumbnail;
        }

        private static String imageUrl(String url, String contextPath) {
            return url != null && url.startsWith("/uploads/products/") ? contextPath + url : url;
        }
    }
}
