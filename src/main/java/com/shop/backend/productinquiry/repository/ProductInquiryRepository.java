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

    // 판매자 본인 상품에 달린 문의 목록 (S-12)
    // 내 상품(seller_id=나)들의 product_id에 해당하는 문의만
    @Query("select i from ProductInquiry i " +
           "where i.product_id in (select p.id from Product p where p.seller_id = :sellerId) " +
           "order by i.created_at desc")
    Page<ProductInquiry> findBySellerProducts(@Param("sellerId") String sellerId, Pageable pageable);
}
