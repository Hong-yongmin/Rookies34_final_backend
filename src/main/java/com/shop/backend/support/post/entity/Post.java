
package com.shop.backend.support.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "board_post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer post_id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardType board_type;

    @Enumerated(EnumType.STRING)
    private FaqCategory faq_category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean is_pinned;

    @Column(nullable = false)
    private LocalDateTime created_at;

    public Post() {
    }
}

