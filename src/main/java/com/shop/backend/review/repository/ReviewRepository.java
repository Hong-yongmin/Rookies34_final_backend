
package com.shop.backend.review.repository;

import com.shop.backend.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query("SELECT r FROM Review r WHERE r.product_id = :productId")
    Page<Review> findReviewsByProductId(
            @Param("productId") Integer productId,
            Pageable pageable
    );

    // 메서드 이름 방식은 '_'를 연관 필드 구분자로 해석하므로 @Query로 작성
    @Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.order_item_id = :orderItemId")
    boolean existsByOrderItemId(@Param("orderItemId") Integer orderItemId);
}

