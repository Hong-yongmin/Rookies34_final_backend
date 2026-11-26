
package com.shop.backend.support.inquiry.repository;

import com.shop.backend.support.inquiry.entity.Inquiry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InquiryRepository
        extends JpaRepository<Inquiry, Integer> {

    // 특정 사용자의 문의 목록 조회 (최신순, 페이지네이션)
    @Query("""
        SELECT i
        FROM Inquiry i
        WHERE i.user_id = :userId
        ORDER BY i.created_at DESC
    """)
    List<Inquiry> findInquiriesByUserId(
            @Param("userId") String userId,
            org.springframework.data.domain.Pageable pageable
    );

    // 특정 사용자의 전체 문의 개수
    long countByUser_id(String userId);
}
