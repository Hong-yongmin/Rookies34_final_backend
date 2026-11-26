
package com.shop.backend.review.repository;

import com.shop.backend.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query("SELECT r FROM Review r WHERE r.product_id = :productId ORDER BY r.created_at DESC")
    List<Review> findReviewsByProductId(@Param("productId") Integer productId);

}
