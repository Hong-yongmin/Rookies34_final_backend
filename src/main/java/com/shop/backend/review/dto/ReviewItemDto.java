
package com.shop.backend.review.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class ReviewItemDto {

    private Integer review_id;
    private Integer rating;
    private String content;
    private List<String> images;
    private String writer;
    private String created_at;

    public ReviewItemDto(
            Integer review_id,
            Integer rating,
            String content,
            List<String> images,
            String writer,
            String created_at
    ) {
        this.review_id = review_id;
        this.rating = rating;
        this.content = content;
        this.images = images;
        this.writer = writer;
        this.created_at = created_at;
    }
}
