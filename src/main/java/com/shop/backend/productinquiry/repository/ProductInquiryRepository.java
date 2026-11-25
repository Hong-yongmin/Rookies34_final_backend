package com.shop.backend.productinquiry.repository;

import com.shop.backend.productinquiry.entity.ProductInquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductInquiryRepository extends JpaRepository<ProductInquiry, Long> {

    // 특정 상품의 문의 목록 (U-26)
    @Query("select i from ProductInquiry i where i.product_id = :productId")
    Page<ProductInquiry> findByProduct(@Param("productId") Long productId, Pageable pageable);
}
