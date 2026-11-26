package com.shop.backend.auth.service;

import com.shop.backend.auth.dto.AuthResponse;
import com.shop.backend.auth.dto.FindIdResponse;
import com.shop.backend.auth.dto.LoginRequest;
import com.shop.backend.auth.repository.AuthRepository;
import com.shop.backend.global.security.JwtUtil;
import com.shop.backend.member.dto.LoginResponse;
import com.shop.backend.member.dto.MemberResponse;
import com.shop.backend.member.repository.AdminRepository;
import com.shop.backend.member.repository.SellerRepository;
import com.shop.backend.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final AdminRepository adminRepository;

    private final JwtUtil jwtUtil;
    private final AuthRepository authRepository;

    public LoginResponse login(LoginRequest req){
        if( userRepository.loginUser(req.getLogin_id(), req.getPassword())){
            return LoginResponse.builder()
                    .code("user")
                    .token(jwtUtil.generateToken(req.getLogin_id())).build();
        }
        else if( sellerRepository.loginSeller(req.getLogin_id(), req.getPassword())){
            return LoginResponse.builder()
                    .code("seller")
                    .token(jwtUtil.generateToken(req.getLogin_id())).build();
        }
        return LoginResponse.builder()
                .code("no").build();
    }

    public LoginResponse loginAdmin(LoginRequest req){
        if( adminRepository.loginAdmin(req.getLogin_id(), req.getPassword())){
            return LoginResponse.builder()
                    .code("admin")
                    .token(jwtUtil.generateToken(req.getLogin_id())).build();
        }
        return LoginResponse.builder()
                .code("no").build();
    }

    public MemberResponse checkId(String id){
        // 로그인 페이지가 공통이므로 id는 user와 seller 모두에서 unique한지 확인
        if( userRepository.checkUserId(id) && sellerRepository.checkSellerId(id)){
            return MemberResponse.builder().code("ok").build();
        }
        return MemberResponse.builder().code("no").build();
    }

    public FindIdResponse findUserIdByPhone(String phone){
        return FindIdResponse.builder().code("ok").id(authRepository.findUserIdByPhone(phone)).build();
    }

    public FindIdResponse findUserIdByEmail(String email){
        return FindIdResponse.builder().code("ok").id(authRepository.findUserIdByEmail(email)).build();
    }

    public FindIdResponse findSellerIdByPhone(String phone){
        return FindIdResponse.builder().code("ok").id(authRepository.findSellerIdByPhone(phone)).build();
    }

    public FindIdResponse findSellerIdByEmail(String email){
        return FindIdResponse.builder().code("ok").id(authRepository.findSellerIdByEmail(email)).build();
    }

    public AuthResponse resetPassword(String id, String password){
        if(authRepository.resetPassword(id, password)){
            return AuthResponse.builder().code("ok").build();
        }
        return AuthResponse.builder().code("no").build();
    }

}
