package com.shop.backend.member.controller;

import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.UserChecker;
import com.shop.backend.member.dto.*;
import com.shop.backend.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final UserChecker userChecker;

    @GetMapping("/users/check-id")
    public ResponseEntity<MemberResponse> checkId(@RequestParam(required = false) String login_id){
        return ResponseEntity.status(HttpStatus.OK).body(userService.checkId(login_id));
    }

    @PostMapping("/users")
    public ResponseEntity<MemberResponse> signup(@RequestBody UserSignupRequest req){
        return ResponseEntity.status(HttpStatus.OK).body(userService.signupUser(req));
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> myInfo(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id){
        if (!userChecker.isUser(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserInfo(id));
    }

    @PutMapping("/me")
    public ResponseEntity<MemberResponse> changeMyInfo(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @RequestBody UserUpdateRequest req){
        if (!userChecker.isUser(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.changeUserInfo(id, req));
    }

    @PutMapping("/me/password")
    public ResponseEntity<MemberResponse> changeMyPassword(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @RequestBody PasswordChangeRequest req){
        if (!userChecker.isUser(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.changeUserPassword(id, req));
    }
}
