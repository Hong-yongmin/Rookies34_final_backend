
package com.shop.backend.support.inquiry.repository;

import com.shop.backend.support.inquiry.entity.Inquiry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InquiryRepository
        extends JpaRepository<Inquiry, Integer> {

    @Query(
            value = "SELECT i FROM Inquiry i ORDER BY i.created_at DESC, i.inquiry_id DESC",
            countQuery = "SELECT COUNT(i) FROM Inquiry i"
    )
    Page<Inquiry> findAllForAdmin(Pageable pageable);

    // 특정 사용자의 문의 목록 조회 (최신순, 페이지네이션)
    @Query("""
        SELECT i
        FROM Inquiry i
        WHERE i.user_id = :userId
        ORDER BY i.created_at DESC
    """)
    List<Inquiry> findInquiriesByUserId(
            @Param("userId") String userId,
            Pageable pageable
    );

    // 특정 사용자의 전체 문의 개수
    // 메서드 이름 방식은 '_'를 연관 필드 구분자로 해석하므로 @Query로 작성
    @Query("SELECT COUNT(i) FROM Inquiry i WHERE i.user_id = :userId")
    long countByUserId(@Param("userId") String userId);
}
