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
            @RequestBody AuthRequest req){
        return ResponseEntity.ok(AuthResponse.builder()
                        .code("ok")
                        .verify_code("123")
                        .build());
    }

    @PostMapping("/auth/find-id/sms/verify")
    public ResponseEntity<FindIdResponse> findIdAuthSmsVerify(
            @RequestBody AuthRequest req){
        return ResponseEntity.ok(authService.findUserIdByPhone(req.getPhone()));
    }

    @PostMapping("/auth/find-id/email")
    public ResponseEntity<AuthResponse> findIdAuthEmail(
            @RequestBody AuthRequest req){
        return ResponseEntity.ok(AuthResponse.builder()
                        .code("ok")
                        .verify_code("123")
                        .build());
    }

    @PostMapping("/auth/find-id/email/verify")
    public ResponseEntity<FindIdResponse> findIdAuthEmailVerify(
            @RequestBody AuthRequest req
    ){
        return ResponseEntity.ok(authService.findUserIdByEmail(req.getEmail()));
    }

    @PostMapping("/auth/password-reset/sms")
    public ResponseEntity<AuthResponse> resetPwdAuthSms(
            @RequestBody AuthRequest req){
        return ResponseEntity.ok(AuthResponse.builder()
                        .code("ok")
                        .verify_code("123")
                        .build());
    }

    @PostMapping("/auth/password-reset/sms/verify")
    public ResponseEntity<ResetPasswordResponse> resetPwdAuthSmsVerify(
            @RequestBody AuthRequest req){
        return ResponseEntity.ok(ResetPasswordResponse.builder()
                        .code("ok")
                        .reset_token(jwtUtil.generateToken(req.getLogin_id()))
                        .build());
    }

    @PostMapping("/auth/password-reset/email")
    public ResponseEntity<AuthResponse> resetPwdAuthEmail(
            @RequestBody AuthRequest req){
        return ResponseEntity.ok(AuthResponse.builder()
                        .code("ok")
                        .verify_code("123")
                        .build());
    }

    @PostMapping("/auth/password-reset/email/verify")
    public ResponseEntity<ResetPasswordResponse> resetPwdAuthEmailVerify(
            @RequestBody AuthRequest req) {
        return ResponseEntity.ok(ResetPasswordResponse.builder()
                .code("ok")
                .reset_token(jwtUtil.generateToken(req.getLogin_id()))
                .build());
    }

    @PostMapping("/auth/password-reset")
    public ResponseEntity<AuthResponse> resetPwd(
            @RequestBody AuthRequest req
    ){
        if(Objects.equals(req.getNew_password(), req.getNew_password_confirm())
                && jwtUtil.extractClaims(req.getReset_token()) != null) {
            return ResponseEntity.ok(authService.resetPassword(jwtUtil.extractClaims(req.getReset_token()), req.getNew_password()));
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
