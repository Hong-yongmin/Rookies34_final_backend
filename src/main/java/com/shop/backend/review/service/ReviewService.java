
package com.shop.backend.review.service;

import com.shop.backend.review.repository.ReviewRepository;
import com.shop.backend.review.repository.ReviewImageRepository;
import com.shop.backend.review.repository.ReviewWriterRepository;
import com.shop.backend.review.entity.Review;
import com.shop.backend.review.entity.ReviewImage;
import com.shop.backend.review.dto.ReviewItemDto;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewWriterRepository reviewWriterRepository;
    public List<Review> getReviewsByProductId(Integer productId) {

        return reviewRepository.findReviewsByProductId(productId);

    }

    public List<ReviewItemDto> getReviewItems(Integer productId) {

        List<Review> reviews = getReviewsByProductId(productId);

        return reviews.stream()
                .map(review -> new ReviewItemDto(
                        review.getReview_id(),
                        review.getRating(),
                        review.getContent(),
                        getReviewImages(review.getReview_id()),
                        reviewWriterRepository.findWriterName(review.getUser_id()),
                        review.getCreated_at().toString()
                ))
                .toList();
    }

    public List<String> getReviewImages(Integer reviewId) {

        List<ReviewImage> images = reviewImageRepository.findByReviewIds(
                List.of(reviewId)
        );

        return images.stream()
                .map(ReviewImage::getUrl)
                .toList();
    }

}
