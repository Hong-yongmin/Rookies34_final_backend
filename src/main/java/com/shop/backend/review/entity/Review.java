
package com.shop.backend.review.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer review_id;

    @Column(nullable = false, length = 50)
    private String user_id;

    @Column(nullable = false)
    private Integer product_id;

    @Column(nullable = false, unique = true)
    private Integer order_item_id;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime created_at;

    @Column(nullable = false)
    private Integer order_id;

    public Review() {}
}
