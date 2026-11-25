package com.shop.backend.member.service;

import com.shop.backend.member.dto.*;
import com.shop.backend.member.repository.SellerRepository;
import com.shop.backend.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

    private MemberResponse ok(){ return MemberResponse.builder().code("ok").build(); }
    private MemberResponse no(){ return MemberResponse.builder().code("no").build(); }

    private boolean isBlank(String v){ return v == null || v.isBlank(); }

    public MemberResponse checkId(String login_id){
        // 구매자·판매자 공용 두 테이블 모두 확인
        if (isBlank(login_id)){
            return no();
        }
        if (userRepository.checkUserId(login_id) && sellerRepository.checkSellerId(login_id)){
            return ok();
        }
        return no();
    }

    public MemberResponse signupUser(UserSignupRequest req){
        // 필수 항목 확인
        if (isBlank(req.getLogin_id()) || isBlank(req.getPassword()) || isBlank(req.getName())
                || isBlank(req.getEmail()) || isBlank(req.getPhone())
                || isBlank(req.getZipcode()) || isBlank(req.getAddress())){
            return no();
        }
        // 약관 동의, 만 14세 이상은 true여야 가입 가능
        if (!Boolean.TRUE.equals(req.getAgree_terms()) || !Boolean.TRUE.equals(req.getAgree_privacy())
                || !Boolean.TRUE.equals(req.getIs_over_14())){
            return no();
        }
        if (checkId(req.getLogin_id()).getCode().equals("no")){ // 아이디 중복
            return no();
        }
        if ("NONE".equals(req.getGender())){ // DB enum은 MALE, FEMALE만 존재 -> NONE은 NULL로 저장
            req.setGender(null);
        }

        if (userRepository.signupUser(req)){
            return ok();
        }
        return no();
    }

    public UserInfoResponse getUserInfo(String id){
        return UserInfoResponse.builder()
                .code("ok")
                .userInfo(userRepository.getUserInfo(id))
                .build();
    }

    public MemberResponse changeUserInfo(String id, UserUpdateRequest req){
        if (isBlank(req.getCurrent_password()) || isBlank(req.getName())
                || isBlank(req.getEmail()) || isBlank(req.getPhone())){
            return no();
        }
        if (!userRepository.checkUserPassword(id, req.getCurrent_password())){ // 현재 비밀번호로 재인증
            return no();
        }
        String gender = "NONE".equals(req.getGender()) ? null : req.getGender();

        if (userRepository.changeUserInfo(id, UserInfo.builder()
                .name(req.getName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .gender(gender)
                .birth_date(req.getBirth_date())
                .build())){
            return ok();
        }
        return no();
    }

    public MemberResponse changeUserPassword(String id, PasswordChangeRequest req){
        if (isBlank(req.getCurrent_password()) || isBlank(req.getNew_password())){
            return no();
        }
        if (!userRepository.checkUserPassword(id, req.getCurrent_password())){
            return no();
        }
        if (userRepository.changeUserPassword(id, req.getNew_password())){
            return ok();
        }
        return no();
    }
}
