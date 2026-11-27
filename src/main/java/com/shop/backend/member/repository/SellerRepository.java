package com.shop.backend.member.repository;

import com.shop.backend.global.common.CommonFunction;
import com.shop.backend.member.dto.SellerInfo;
import com.shop.backend.member.dto.SellerSignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SellerRepository {
    private final JdbcTemplate jdbcTemplate;
    private final CommonFunction cf;
    private static Double DEFAULT_RATE = 0.05;

    public Boolean loginSeller(String id, String password){
        /*
         * 판매자 로그인
         */
        String sql = "SELECT 1 FROM seller WHERE seller_id='%s' AND password='%s'".formatted(id, password);
        return !jdbcTemplate.queryForList(sql).isEmpty();
    }

    public Boolean checkSellerId(String id){
        /*
         * 판매자 id 중복여부 확인
         * 중복 시 false, 중복되지 않으면 true 반환
         */
        String sql = "SELECT 1 FROM seller WHERE seller_id='"+id+"'";
        return jdbcTemplate.queryForList(sql).size() == 0;
    }

    public Boolean checkSellerPassword(String id, String password){
        /*
         * 판매자 현재 비밀번호 확인
         * 일치하면 true 반환
         */
        String sql = "SELECT 1 FROM seller WHERE seller_id='"+id+"' AND password='"+password+"'";
        return jdbcTemplate.queryForList(sql).size() > 0;
    }

    public Boolean signupSeller(SellerSignupRequest req){
        /*
         * 판매자 회원가입 쿼리
         * 필수 항목은 함수 호출 전 존재 여부 확인 필요
         * 쿼리 실행 성공 시 true 반환
         */
        String sql = "INSERT INTO seller (seller_id, password, manager_name, email, phone, business_reg_no, " +
                "business_name, cert_file_url, status, bank, account_no, account_holder) VALUES("
                + cf.q(req.getLogin_id()) + "," + cf.q(req.getPassword()) + "," + cf.q(req.getManager_name()) + ","
                + cf.q(req.getEmail()) + "," + cf.q(req.getPhone()) + "," + cf.q(req.getBusiness_reg_no()) + ","
                + cf.q(req.getBusiness_name()) + "," + cf.q(req.getCert_file_path()) + ", 'PENDING', "
                + cf.q(req.getBank()) + "," + cf.q(req.getAccount_no()) + "," + cf.q(req.getAccount_holder()) + ")";
        if(jdbcTemplate.update(sql) <= 0){
            return false;
        }
        return setCommission(req.getLogin_id());
    }

    public Boolean setCommission(String id){
        /*
         * 판매자 기본 수수료율 설정 (0.05)
         * 쿼리 실행 성공 시 true 반환
         */
        String sql = "INSERT INTO commission_rate (seller_id, commission_rate) VALUES ('%s', %f)".formatted(id, DEFAULT_RATE);

        return jdbcTemplate.update(sql) > 0;
    }

    public Boolean changeSellerPassword(String id, String password){
        /*
         * 판매자 비밀번호 변경
         * 쿼리 실행 성공 시 true 반환
         */
        String sql = "UPDATE seller SET password='"+password+"' WHERE seller_id='"+id+"'";
        int rows = jdbcTemplate.update(sql);
        return rows > 0;
    }

    public SellerInfo getSellerInfo(String id){
        /*
         * 판매자 정보 조회
         */
        String sql = "SELECT * FROM seller WHERE seller_id='"+id+"'";
        return jdbcTemplate.queryForObject(sql,
                (rs, i) -> SellerInfo.builder()
                        .manager_name(rs.getString("manager_name"))
                        .email(rs.getString("email"))
                        .phone(rs.getString("phone"))
                        .business_reg_no(rs.getString("business_reg_no"))
                        .business_name(rs.getString("business_name"))
                        .cert_file_url(rs.getString("cert_file_url"))
                        .bank(rs.getString("bank"))
                        .account_no(rs.getString("account_no"))
                        .account_holder(rs.getString("account_holder"))
                        .status(rs.getString("status"))
                        .created_at(rs.getString("created_at")).build());
    }

    public Boolean changeSellerInfo(String id, SellerInfo req){
        /*
         * 판매자 정보 수정
         */
        String sql = "UPDATE seller SET "
                + "manager_name=" + cf.q(req.getManager_name()) + ","
                + "email=" + cf.q(req.getEmail()) + ","
                + "phone=" + cf.q(req.getPhone()) + ","
                + "business_reg_no=" + cf.q(req.getBusiness_reg_no()) + ","
                + "business_name=" + cf.q(req.getBusiness_name()) + ","
                + "cert_file_url=" + cf.q(req.getCert_file_url()) + ","
                + "bank=" + cf.q(req.getBank()) + ","
                + "account_no=" + cf.q(req.getAccount_no()) + ","
                + "account_holder=" + cf.q(req.getAccount_holder()) + " "
                + "WHERE seller_id='" + id + "'";
        int rows = jdbcTemplate.update(sql);
        return rows > 0;
    }
}
