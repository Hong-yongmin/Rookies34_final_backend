
package com.shop.backend.review.repository;

import com.shop.backend.review.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Integer> {

    @Query("SELECT ri FROM ReviewImage ri WHERE ri.review_id IN :reviewIds ORDER BY ri.image_id")
    List<ReviewImage> findByReviewIds(@Param("reviewIds") List<Integer> reviewIds);

}
