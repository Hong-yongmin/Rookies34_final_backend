package com.shop.backend.member.controller;

import com.shop.backend.member.service.AdminService;
import com.shop.backend.member.service.SellerService;
import com.shop.backend.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final SellerService sellerService;
    private final AdminService adminService;
}
