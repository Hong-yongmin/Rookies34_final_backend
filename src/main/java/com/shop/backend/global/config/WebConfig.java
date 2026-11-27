package com.shop.backend.global.config;

import com.shop.backend.review.service.ReviewImageStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 상품 이미지 절대경로 (properties 없으면 기본값 사용)
    @Value("${file.product-upload-dir:/var/shop/uploads/products}")
    private String productUploadDir;

    @Value("${file.review-upload-dir:/var/shop/uploads/reviews}")
    private String reviewUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 상품 이미지만 공개 서빙 (certs 등 다른 폴더는 노출 안 됨)
        registry.addResourceHandler("/uploads/products/**")              // 이 URL로 오면
                .addResourceLocations("file:" + productUploadDir + "/"); // 이 절대경로에서 찾아 줌
        registry.addResourceHandler(ReviewImageStorage.URL_PREFIX + "**")
                .addResourceLocations(java.nio.file.Path.of(reviewUploadDir).toAbsolutePath()
                        .normalize().toUri().toASCIIString().replaceFirst("/?$", "/"));
    }
}
