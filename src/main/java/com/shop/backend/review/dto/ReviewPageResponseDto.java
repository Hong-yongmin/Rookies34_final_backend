
package com.shop.backend.review.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReviewPageResponseDto {

    private final long totalElements;
    private final int totalPages;
    private final int currentPage;
    private final List<ReviewItemDto> reviews;

}
