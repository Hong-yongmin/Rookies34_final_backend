package com.shop.backend.member.repository;
import com.shop.backend.address.dto.AddressCreateRequest;
import com.shop.backend.address.repository.AddressRepository;
import com.shop.backend.member.dto.UserInfo;
import com.shop.backend.member.dto.UserSignupRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final AddressRepository addressRepository;

    public Boolean checkUserId(String id){
        /*
         * 일반 사용자 id 중복여부 확인
         * 중복 시 false, 중복되지 않으면 true 반환
         */
        String sql = "SELECT 1 FROM user WHERE user_id='"+id+"'";
        return jdbcTemplate.queryForList(sql).size() == 0;
    }

    public Boolean signupUser(UserSignupRequest req){
        /*
        * 일반 사용자 회원가입 쿼리
        * 필수 항목은 함수 호출 전 존재 여부 확인 필요
        * 쿼리 실행 성공 시 true 반환
         */
        String sql = "INSERT INTO user (user_id, password, name, email, phone, gender, birth_date, point, status) VALUES ("
                + req.getLogin_id() + "," + req.getPassword() + "," + req.getName() + "," + req.getEmail() + ","
                + req.getPhone() + "," + req.getGender() + "," + req.getBirth_date() + ", 0, 'ACTIVE')";
        int rows = jdbcTemplate.update(sql);
        if( rows <= 0 ){
            return false;
        }

        return addressRepository.addAddress(AddressCreateRequest.builder() // 주소는 별도 추가
                        .recipient_name(req.getName())
                        .recipient_phone(req.getPhone())
                        .zipcode(req.getZipcode())
                        .address(req.getAddress())
                        .address_detail(req.getAddress_detail())
                        .is_default(false)                                  // 회원가입 시
                        .build());
    }

    public Boolean changeUserPassword(String id, String password){
        /*
        * 일반 사용자 비밀번호 변경
        * 쿼리 실행 성공 시 true 반환
         */
        String sql = "UPDATE user SET password='"+password+"' WHERE user_id=id";
        int rows = jdbcTemplate.update(sql);
        return rows > 0;
    }

    public UserInfo getUserInfo(String id){
        /*
         * 일반 사용자 회원정보 조회
         */
        String sql = "SELECT * FROM user WHERE user_id='"+id+"'";
        return jdbcTemplate.queryForObject(sql, UserInfo.class);
    }

    public Boolean changeUserInfo(String id, UserInfo req){
        /*
         * 일반 사용자 회원정보 수정
         */
        String sql = "UPDATE user SET "
                + "name='"+req.getName()+"',"
                + "email='"+req.getEmail()+"',"
                + "phone='"+req.getPhone()+"',"
                + "gender='"+req.getGender()+"',"
                + "birth_date='"+req.getBirth_date()+"'";
        int rows = jdbcTemplate.update(sql);
        return rows > 0;
    }
}
