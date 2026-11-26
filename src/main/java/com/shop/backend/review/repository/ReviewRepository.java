
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

    boolean existsByOrder_item_id(Integer order_item_id);
}


