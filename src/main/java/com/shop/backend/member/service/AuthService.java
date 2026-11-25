package com.shop.backend.member.service;

import com.shop.backend.global.security.JwtUtil;
import com.shop.backend.member.dto.LoginRequest;
import com.shop.backend.member.dto.LoginResponse;
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

}
