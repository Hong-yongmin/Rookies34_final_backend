package com.shop.backend.member.repository;
import com.shop.backend.member.dto.SellerSignupRequest;
import com.shop.backend.member.dto.UserSignupRequest;
import com.shop.backend.global.common.CommonFunction;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final CommonFunction cf;

    public Boolean checkUserId(String id){
        /*
         * 일반 사용자 id 중복여부 확인
         * 중복 시 false, 중복되지 않으면 true 반환
         */
        String sql = "SELECT login_id FROM user WHERE login_id='"+id+"'";
        List<String> result= jdbcTemplate.queryForList(sql, String.class);

        return !result.isEmpty(); // 비어있으면 true 반환
    }

    public Boolean checkSellerId(String id){ // SellerRepository로 이동
        /*
        * 판매자 id 중복여부 확인
        * 중복 시 false, 중복되지 않으면 true 반환
         */
        String sql = "SELECT login_id FROM seller WHERE login_id='"+id+"'";
        List<String> result= jdbcTemplate.queryForList(sql, String.class);

        return !result.isEmpty(); // 비어있으면 true 반환
    }

    public Boolean signupUser(UserSignupRequest req){
        /*
        * 일반 사용자 회원가입 쿼리
        * 필수 항목은 함수 호출 전 존재 여부 확인 필요
        * 쿼리 실행 성공 시 true 반환
         */
        String sql = "INSERT INTO user (login_id, password, name, email, phone, zipcode, address, address_detail, gender, birth_date) VALUES ("
                + cf.q(req.getLogin_id()) + "," + cf.q(req.getPassword()) + "," + cf.q(req.getName()) + "," + cf.q(req.getEmail()) + ","
                + cf.q(req.getPhone()) + "," + cf.q(req.getZipcode()) + "," + cf.q(req.getAddress()) + "," + cf.q(req.getAddress_detail()) + ","
                + cf.q(req.getGender()) + "," + cf.q(req.getBirth_date()) + ")";
        int rows = jdbcTemplate.update(sql);
        return rows > 0;
    }

    public Boolean signupSeller(SellerSignupRequest req){ // SellerRepository로 이동
        /*
        * 판매자 회원가입 쿼리
        * 필수 항목은 함수 호출 전 존재 여부 확인 필요
        * 쿼리 실행 성공 시 true 반환
         */
        String sql = "INSERT INTO seller (login_id, password, manager_name, email, phone, business_reg_no, business_name, cert_file_path) VALUES("
                + cf.q(req.getLogin_id()) + "," + cf.q(req.getPassword()) + "," + cf.q(req.getManager_name()) + ","
                + cf.q(req.getEmail()) + "," + cf.q(req.getPhone()) + "," + cf.q(req.getBusiness_reg_no()) + ","
                + cf.q(req.getBusiness_name()) + "," + cf.q(req.getCert_file_path()) + ")";
        int rows = jdbcTemplate.update(sql);
        return rows > 0;
    }

    public Boolean changeUserPassword(String id, String password){
        /*
        * 일반 사용자 비밀번호 변경
        * 쿼리 실행 성공 시 true 반환
         */
        String sql = "UPDATE user SET password='"+password+"' WHERE login_id=id";
        int rows = jdbcTemplate.update(sql);
        return rows > 0;
    }

    public Boolean changeSellerPassword(String id, String password){ // SellerRepository로 이동
        /*
         * 일반 사용자 비밀번호 변경
         * 쿼리 실행 성공 시 true 반환
         */
        String sql = "UPDATE seller SET password='"+password+"' WHERE login_id=id";
        int rows = jdbcTemplate.update(sql);
        return rows > 0;
    }
}
