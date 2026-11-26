
package com.shop.backend.review.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "review_image")
public class ReviewImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer image_id;

    @Column(nullable = false)
    private Integer review_id;

    @Column(nullable = false, length = 2048)
    private String url;

    public ReviewImage() {}
}

