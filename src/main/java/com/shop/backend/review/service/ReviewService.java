
package com.shop.backend.review.service;

import com.shop.backend.review.repository.ReviewRepository;
import com.shop.backend.review.repository.ReviewImageRepository;
import com.shop.backend.review.repository.ReviewWriterRepository;
import com.shop.backend.review.entity.Review;
import com.shop.backend.review.entity.ReviewImage;
import com.shop.backend.review.dto.ReviewItemDto;
import com.shop.backend.review.dto.ReviewPageResponseDto;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewWriterRepository reviewWriterRepository;
    public Page<Review> getReviewsByProductId(Integer productId, int page) {

        PageRequest pageable = PageRequest.of(
                page,
                10,
                Sort.by(Sort.Direction.DESC, "created_at")
        );

        return reviewRepository.findReviewsByProductId(productId, pageable);

    }

    public ReviewPageResponseDto getReviewItems(Integer productId, int page) {

        Page<Review> reviews = getReviewsByProductId(productId, page);

        List<ReviewItemDto> reviewItems = reviews.getContent().stream()
                .map(review -> new ReviewItemDto(
                        review.getReview_id(),
                        review.getRating(),
                        review.getContent(),
                        getReviewImages(review.getReview_id()),
                        reviewWriterRepository.findWriterName(review.getUser_id()),
                        review.getCreated_at().toString()
                ))
                .toList();

        return new ReviewPageResponseDto(
                reviews.getTotalElements(),
                reviews.getTotalPages(),
                reviews.getNumber(),
                reviewItems
        );
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
