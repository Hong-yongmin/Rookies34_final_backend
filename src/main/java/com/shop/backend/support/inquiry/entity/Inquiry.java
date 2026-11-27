
package com.shop.backend.support.inquiry.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "cs_inquiry")
public class Inquiry {

    public static final int MAX_TEXT_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer inquiry_id;

    @Column(nullable = false, length = 50)
    private String user_id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryCategory category;

    private Integer order_id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = MAX_TEXT_LENGTH)
    private String content;

    @Column(length = 2048)
    private String attachment_url;

    @Column(length = MAX_TEXT_LENGTH)
    private String answer;

    private LocalDateTime answered_at;

    @Column(nullable = false)
    private LocalDateTime created_at;

    public Inquiry() {
    }
}
