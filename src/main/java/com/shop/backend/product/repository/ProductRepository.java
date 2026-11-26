package com.shop.backend.product.repository;

import com.shop.backend.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 카테고리별 목록
    @Query("select p from Product p where p.category_id = :categoryId")
    Page<Product> findByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    // 검색 (상품명에 keyword 포함)
    Page<Product> findByNameContaining(String keyword, Pageable pageable);

    // 큐레이션별 (product 테이블 플래그)
    Page<Product> findByBestTrue(Pageable pageable);
    Page<Product> findBySaleTrue(Pageable pageable);
    Page<Product> findByBenefitTrue(Pageable pageable);

    @Query("select p from Product p where p.new_arrival = true")
    Page<Product> findNewArrivals(Pageable pageable);

    // 판매자 본인 상품 목록 (S-11) — status/keyword 선택 필터
    @Query("select p from Product p where p.seller_id = :sellerId " +
           "and (:status is null or p.status = :status) " +
           "and (:keyword is null or p.name like concat('%', :keyword, '%'))")
    Page<Product> findSellerProducts(@Param("sellerId") String sellerId,
                                     @Param("status") String status,
                                     @Param("keyword") String keyword,
                                     Pageable pageable);
}
