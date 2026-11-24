package com.shop.backend.member.controller;

import com.shop.backend.member.dto.LoginRequest;
import com.shop.backend.member.dto.LoginResponse;
import com.shop.backend.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

//    @PostMapping("/auth/login")
//    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
//
//    }
}
