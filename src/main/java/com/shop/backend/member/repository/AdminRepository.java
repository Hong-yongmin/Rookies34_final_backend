package com.shop.backend.member.repository;

import com.shop.backend.member.dto.AdminMemberInfo;
import com.shop.backend.member.dto.SellerApprovalRequest;
import com.shop.backend.member.dto.UserStatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminRepository {

    private final JdbcTemplate jdbcTemplate;

    public Boolean loginAdmin(String id, String password){
        /*
         * 관리자 로그인
         */
        String sql = "SELECT 1 FROM admin WHERE admin_id='%s' AND password='%s'".formatted(id, password);
        return !jdbcTemplate.queryForList(sql).isEmpty();
    }

    public List<AdminMemberInfo> getUsersInfo(String keyword, String status, Integer page){
        /*
         * 관리자의 사용자 정보 조회
         * keyword, status는 기본 모든 정보 조회, 값이 있을 경우 조건 추가
         */
        if (keyword == null) keyword = ""; // keyword 없으면 전체 조회
        String sql = "SELECT * FROM user WHERE ((user_id LIKE '%%%s%%') OR (name LIKE '%%%s%%')) ".formatted(keyword, keyword);
        if (status != null){
            sql += "AND status='%s' ".formatted(status);
        }
        if (page != null && page > 0){
            sql += "LIMIT 10 OFFSET %d".formatted((page-1)*10);
        }
        return jdbcTemplate.query(sql,
                (rs, i) -> AdminMemberInfo.builder()
                        .user_id(rs.getString("user_id"))
                        .login_id(rs.getString("user_id"))
                        .name(rs.getString("name"))
                        .role("BUYER")
                        .status(rs.getString("status"))
                        .created_at(rs.getString("created_at")).build());
    }

    public List<AdminMemberInfo> getSellersInfo(String keyword, String status, Integer page){
        /*
         * 관리자의 판매자 정보 조회
         * keyword, status는 기본 모든 정보 조회, 값이 있을 경우 조건 추가
         */
        if (keyword == null) keyword = ""; // keyword 없으면 전체 조회
        String sql = "SELECT * FROM seller WHERE ((seller_id LIKE '%%%s%%') OR (manager_name LIKE '%%%s%%') OR (business_name LIKE '%%%s%%')) ".formatted(keyword, keyword, keyword);
        if (status != null){
            sql += "AND status='%s' ".formatted(status);
        }
        if (page != null && page > 0){
            sql += "LIMIT 10 OFFSET %d".formatted((page-1)*10);
        }
        return jdbcTemplate.query(sql,
                (rs, i) -> AdminMemberInfo.builder()
                        .user_id(rs.getString("seller_id"))
                        .login_id(rs.getString("seller_id"))
                        .name(rs.getString("manager_name"))
                        .role("SELLER")
                        .status(rs.getString("status"))
                        .created_at(rs.getString("created_at")).build());
    }

    public Boolean changeUserStatus(String id, UserStatusRequest req){
        /*
         * 사용자 상태 변경
         * memo가 있으면 reason 테이블에 사유 저장
         */
        String status = req.getStatus();
        if (!"ACTIVE".equals(status) && !"SUSPENDED".equals(status)){
            return false;
        }

        String sql = "UPDATE user SET status='%s' WHERE user_id='%s'".formatted(status, id);
        int rows = jdbcTemplate.update(sql);
        if (rows <= 0){ // 해당 사용자가 없으면 실패
            return false;
        }

        String memo = req.getMemo();
        if (memo != null && !memo.isBlank()){ // 사유가 있는 경우만 저장
            sql = "INSERT INTO reason (user_id, reason) VALUES ('%s', '%s')".formatted(id, memo);
            rows = jdbcTemplate.update(sql);
            return rows > 0;
        }
        return true;
    }

    public Boolean changeSellerStatus(String id, SellerApprovalRequest req){
        /*
         * 판매자 입점 승인, 거절 등 상태 변경
         * REJECT인 경우 reject_reason 필수, reason 테이블에 사유 저장
         * 쿼리 실행 성공 시 true 반환
         */
        String decision = req.getDecision();
        String reject_reason = req.getReject_reason();
        String status;
        if ("APPROVE".equals(decision)){
            status = "APPROVED";
        } else if ("REJECT".equals(decision)){
            if (reject_reason == null || reject_reason.isBlank()){ // 반려 사유 누락
                return false;
            }
            status = "REJECTED";
        } else {
            return false;
        }

        String sql = "UPDATE seller SET status='%s' WHERE seller_id='%s'".formatted(status, id);
        int rows = jdbcTemplate.update(sql);
        if (rows <= 0){ // 해당 판매자가 없으면 실패
            return false;
        }

        if (status.equals("REJECTED")){ // 반려 시 사유 저장
            sql = "INSERT INTO reason (seller_id, reason) VALUES ('%s', '%s')".formatted(id, reject_reason);
            rows = jdbcTemplate.update(sql);
            return rows > 0;
        }
        return true;
    }
}
