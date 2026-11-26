
package com.shop.backend.support.post.repository;

import com.shop.backend.support.post.entity.Post;
import com.shop.backend.support.post.entity.BoardType;
import com.shop.backend.support.post.entity.FaqCategory;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    // 게시판 유형별 게시글 목록 조회 (페이지네이션)
    @Query("""
        SELECT p
        FROM Post p
        WHERE p.board_type = :boardType
        ORDER BY p.is_pinned DESC, p.created_at DESC
    """)
    List<Post> findByBoardType(
            @Param("boardType") BoardType boardType,
            Pageable pageable
    );

    // 게시판 유형별 전체 게시글 개수
    long countByBoard_type(BoardType boardType);

    // FAQ 분류별 목록 조회 (분류가 null이면 전체 FAQ 조회)
    @Query("""
        SELECT p
        FROM Post p
        WHERE p.board_type = :boardType
        AND (:faqCategory IS NULL OR p.faq_category = :faqCategory)
        ORDER BY p.created_at DESC
    """)
    List<Post> findFaqs(
            @Param("boardType") BoardType boardType,
            @Param("faqCategory") FaqCategory faqCategory,
            Pageable pageable
    );

    // FAQ 전체 개수 또는 분류별 개수
    @Query("""
        SELECT COUNT(p)
        FROM Post p
        WHERE p.board_type = :boardType
        AND (:faqCategory IS NULL OR p.faq_category = :faqCategory)
    """)
    long countFaqs(
            @Param("boardType") BoardType boardType,
            @Param("faqCategory") FaqCategory faqCategory
    );
}
