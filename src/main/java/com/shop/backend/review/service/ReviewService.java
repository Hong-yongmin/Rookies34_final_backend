
package com.shop.backend.review.service;

import com.shop.backend.review.repository.ReviewRepository;
import com.shop.backend.review.repository.ReviewImageRepository;
import com.shop.backend.review.repository.ReviewWriterRepository;
import com.shop.backend.review.repository.ReviewCreateRepository;

import com.shop.backend.review.entity.Review;
import com.shop.backend.review.entity.ReviewImage;

import com.shop.backend.review.dto.ReviewItemDto;
import com.shop.backend.review.dto.ReviewPageResponseDto;
import com.shop.backend.review.dto.ReviewCreateRequest;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewWriterRepository reviewWriterRepository;
    private final ReviewCreateRepository reviewCreateRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getWritableItems(String userId, int page) {
        int pageSize = 10;
        return Map.of(
                "code", "ok",
                "num", reviewCreateRepository.countWritableItems(userId),
                "items", reviewCreateRepository.findWritableItems(userId, pageSize, (page - 1L) * pageSize)
        );
    }

    // 상품 후기 조회
    public Page<Review> getReviewsByProductId(Integer productId, int page) {
        PageRequest pageable = PageRequest.of(
                page, 10, Sort.by(Sort.Direction.DESC, "created_at")
        );
        return reviewRepository.findReviewsByProductId(productId, pageable);
    }

    // 상품 후기 목록
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

    // 후기 이미지 조회
    public List<String> getReviewImages(Integer reviewId) {
        List<ReviewImage> images =
                reviewImageRepository.findByReviewIds(List.of(reviewId));

        return images.stream()
                .map(ReviewImage::getUrl)
                .toList();
    }

    // 주문 상품 및 주문자 검증
    public void validateOrderItem(Integer orderItemId, String userId) {

        Map<String, Object> orderItem =
                reviewCreateRepository.findOrderItem(orderItemId);

        if (!orderItem.get("user_id").toString().equals(userId)) {
            throw new IllegalArgumentException(
                    "본인이 구매한 상품만 리뷰를 작성할 수 있습니다."
            );
        }
    }

    // 중복 후기 검증
    public void validateDuplicateReview(Integer orderItemId) {

        if (reviewRepository.existsByOrderItemId(orderItemId)) {
            throw new IllegalArgumentException(
                    "이미 리뷰를 작성한 상품입니다."
            );
        }
    }

    // 후기 입력값 검증
    public void validateReviewContent(Integer rating, String content) {

        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException(
                    "별점은 1점부터 5점까지 입력할 수 있습니다."
            );
        }

        if (content == null
                || content.trim().length() < 10
                || content.length() > 1000) {
            throw new IllegalArgumentException(
                    "후기 내용은 10자 이상 1000자 이하로 입력해야 합니다."
            );
        }
    }

    // 후기 저장
    public Review createReview(
            ReviewCreateRequest request,
            String userId,
            Map<String, Object> orderItem
    ) {
        Review review = new Review();

        review.setUser_id(userId);
        review.setProduct_id((Integer) orderItem.get("product_id"));
        review.setOrder_item_id(request.getOrder_item_id());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setOrder_id((Integer) orderItem.get("order_id"));
        review.setCreated_at(java.time.LocalDateTime.now());

        return reviewRepository.save(review);
    }

    // 후기 작성 전체 처리
    @Transactional
    public Review createReviewWithValidation(
            ReviewCreateRequest request,
            String userId
    ) {
        try {
            // 1. 주문 상품 조회
            Map<String, Object> orderItem =
                    reviewCreateRepository.findOrderItem(
                            request.getOrder_item_id()
                    );

            // 2. 본인 주문인지 확인
            if (!orderItem.get("user_id").toString().equals(userId)) {
                throw new IllegalArgumentException(
                        "본인이 구매한 상품만 리뷰를 작성할 수 있습니다."
                );
            }

            if (!"COMPLETED".equals(orderItem.get("status")) || "CANCELED".equals(orderItem.get("order_status"))) {
                throw new IllegalArgumentException("배송 완료된 상품만 후기를 작성할 수 있습니다.");
            }

            // 3. 중복 후기 확인
            validateDuplicateReview(request.getOrder_item_id());

            // 4. 별점 및 후기 내용 검증
            validateReviewContent(
                    request.getRating(),
                    request.getContent()
            );

            // 5. 후기 저장
            return createReview(request, userId, orderItem);

        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException(
                    "존재하지 않는 주문 상품입니다."
            );
        }
    }
}
