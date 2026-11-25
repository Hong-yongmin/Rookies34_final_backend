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

    public Boolean checkSellerId(String id){
        /*
         * 판매자 id 중복여부 확인
         * 중복 시 false, 중복되지 않으면 true 반환
         */
        String sql = "SELECT 1 FROM seller WHERE seller_id='"+id+"'";
        return jdbcTemplate.queryForList(sql).size() == 0;
    }

    public Boolean signupSeller(SellerSignupRequest req){
        /*
         * 판매자 회원가입 쿼리
         * 필수 항목은 함수 호출 전 존재 여부 확인 필요
         * 쿼리 실행 성공 시 true 반환
         */
        String sql = "INSERT INTO seller (seller_id, password, manager_name, email, phone, business_reg_no, " +
                "business_name, cert_file_path, status, bank, account_no, account_holder) VALUES("
                + cf.q(req.getLogin_id()) + "," + cf.q(req.getPassword()) + "," + cf.q(req.getManager_name()) + ","
                + cf.q(req.getEmail()) + "," + cf.q(req.getPhone()) + "," + cf.q(req.getBusiness_reg_no()) + ","
                + cf.q(req.getBusiness_name()) + "," + cf.q(req.getCert_file_path()) + ", 'PENDING', "
                + cf.q(req.getBank()) + "," + cf.q(req.getAccount_no()) + "," + cf.q(req.getAccount_holder()) + ")";
        int rows = jdbcTemplate.update(sql);
        return rows > 0;
    }

    public Boolean changeSellerPassword(String id, String password){
        /*
         * 판매자 비밀번호 변경
         * 쿼리 실행 성공 시 true 반환
         */
        String sql = "UPDATE seller SET password='"+password+"' WHERE seller_id=id";
        int rows = jdbcTemplate.update(sql);
        return rows > 0;
    }

    public SellerInfo getSellerInfo(String id){
        /*
         * 판매자 정보 조회
         */
        String sql = "SELECT * FROM seller WHERE seller_id='"+id+"'";
        return jdbcTemplate.queryForObject(sql, SellerInfo.class);
    }

//    public Boolean changeSellerInfo(String id, SellerInfo req){
//        /*
//         * 판매자 정보 수정
//         */
//        //String sql = "UPDATE seller SET "
//         //       + ""
//    }
}
