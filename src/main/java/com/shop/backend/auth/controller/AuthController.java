package com.shop.backend.auth.controller;

import com.shop.backend.auth.dto.*;
import com.shop.backend.auth.service.AuthService;
import com.shop.backend.global.security.JwtUtil;
import com.shop.backend.member.dto.LoginResponse;
import com.shop.backend.member.dto.MemberResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @GetMapping("/auth/check-id")
    public ResponseEntity<MemberResponse> checkId(@RequestParam String login_id){
        return ResponseEntity.ok(authService.checkId(login_id));
    }

    @PostMapping("/auth/find-id/sms")
    public ResponseEntity<AuthResponse> findIdAuthSms(
            @RequestBody String name,
            @RequestBody String phone){
        return ResponseEntity.ok(AuthResponse.builder()
                        .code("ok")
                        .verify_code("123")
                        .build());
    }

    @PostMapping("/auth/find-id/sms/verify")
    public ResponseEntity<FindIdResponse> findIdAuthSmsVerify(
            @RequestBody String phone,
            @RequestBody String name,
            @RequestBody String verify_code){
        return ResponseEntity.ok(authService.findUserIdByPhone(phone));
    }

    @PostMapping("/auth/find-id/email")
    public ResponseEntity<AuthResponse> findIdAuthEmail(
            @RequestBody String name,
            @RequestBody String email){
        return ResponseEntity.ok(AuthResponse.builder()
                        .code("ok")
                        .verify_code("123")
                        .build());
    }

    @PostMapping("/auth/find-id/email/verify")
    public ResponseEntity<FindIdResponse> findIdAuthEmailVerify(
            @RequestBody String email,
            @RequestBody String name,
            @RequestBody String verify_code
    ){
        return ResponseEntity.ok(authService.findUserIdByEmail(email));
    }

    @PostMapping("/auth/password-reset/sms")
    public ResponseEntity<AuthResponse> resetPwdAuthSms(
            @RequestBody String login_id,
            @RequestBody String name,
            @RequestBody String phone){
        return ResponseEntity.ok(AuthResponse.builder()
                        .code("ok")
                        .verify_code("123")
                        .build());
    }

    @PostMapping("/auth/password-reset/sms/verify")
    public ResponseEntity<ResetPasswordResponse> resetPwdAuthSmsVerify(
            @RequestBody String login_id,
            @RequestBody String phone,
            @RequestBody String verify_code){
        return ResponseEntity.ok(ResetPasswordResponse.builder()
                        .code("ok")
                        .reset_token(jwtUtil.generateToken(login_id))
                        .build());
    }

    @PostMapping("/auth/password-reset/email")
    public ResponseEntity<AuthResponse> resetPwdAuthEmail(
            @RequestBody String login_id,
            @RequestBody String name,
            @RequestBody String email){
        return ResponseEntity.ok(AuthResponse.builder()
                        .code("ok")
                        .verify_code("123")
                        .build());
    }

    @PostMapping("/auth/password-reset/email/verify")
    public ResponseEntity<ResetPasswordResponse> resetPwdAuthEmailVerify(
            @RequestBody String login_id,
            @RequestBody String email,
            @RequestBody String verify_code) {
        return ResponseEntity.ok(ResetPasswordResponse.builder()
                .code("ok")
                .reset_token(jwtUtil.generateToken(login_id))
                .build());
    }

    @PostMapping("/auth/password-reset")
    public ResponseEntity<AuthResponse> resetPwd(
            @RequestBody String reset_token,
            @RequestBody String new_password,
            @RequestBody String new_password_confirm
    ){
        if(Objects.equals(new_password, new_password_confirm) && jwtUtil.extractClaims(reset_token) != null) {
            return ResponseEntity.ok(authService.resetPassword(jwtUtil.extractClaims(reset_token), new_password));
        }
        else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰 또는 패스워드 확인 실패");
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req){
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(req));
    }

    @PostMapping("/admin/auth/login")
    public ResponseEntity<LoginResponse> loginAdmin(@RequestBody LoginRequest req){
        return ResponseEntity.status(HttpStatus.OK).body(authService.loginAdmin(req));
    }
}
