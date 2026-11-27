package com.shop.backend.auth.repository;

import com.shop.backend.global.security.SellerChecker;
import com.shop.backend.global.security.UserChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;;

@Repository
@RequiredArgsConstructor
public class AuthRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserChecker userChecker;
    private final SellerChecker sellerChecker;

    public String findUserIdByPhone(String phone){
        /*
         * 휴대폰 번호로 사용자 id 찾기
         */
        String sql = "SELECT user_id FROM user WHERE phone='%s'".formatted(phone);
        try{
            return jdbcTemplate.queryForObject(sql, String.class);
        } catch(EmptyResultDataAccessException e){ // id를 찾지 못하는 경우
            return "no";
        }
    }

    public String findSellerIdByPhone(String phone){
        /*
         * 휴대폰 번호로 판매자 id 찾기
         */
        String sql = "SELECT seller_id FROM seller WHERE phone='%s'".formatted(phone);
        try{
            return jdbcTemplate.queryForObject(sql, String.class);
        } catch(EmptyResultDataAccessException e){ // id를 찾지 못하는 경우
            return "no";
        }
    }

    public String findUserIdByEmail(String email){
        /*
         * 이메일로 사용자 id 찾기
         */
        String sql = "SELECT user_id FROM user WHERE email='%s'".formatted(email);
        try{
            return jdbcTemplate.queryForObject(sql, String.class);
        } catch(EmptyResultDataAccessException e){ // id를 찾지 못하는 경우
            return "no";
        }
    }

    public String findSellerIdByEmail(String email){
        /*
         * 이메일로 사용자 id 찾기
         */
        String sql = "SELECT seller_id FROM seller WHERE email='%s'".formatted(email);
        try{
            return jdbcTemplate.queryForObject(sql, String.class);
        } catch(EmptyResultDataAccessException e){ // id를 찾지 못하는 경우
            return "no";
        }
    }

    public Boolean resetPassword(String id, String password){
        /*
         * 비밀번호 변경
         * id는 사용자와 판매자 모두에서 unique하므로 별도 분리 X
         */
        String sql;
        if(userChecker.isUser(id)){
            sql = "UPDATE user SET password='%s' WHERE user_id='%s'".formatted(password, id);
        }
        else if(sellerChecker.isSeller(id)){
            sql = "UPDATE seller SET password='%s' WHERE seller_id='%s'".formatted(password, id);
        }
        else{
            return false; // 사용자도 판매자도 아닌 경우 false 반환
        }
        return jdbcTemplate.queryForList(sql).size() > 0;
    }
}
