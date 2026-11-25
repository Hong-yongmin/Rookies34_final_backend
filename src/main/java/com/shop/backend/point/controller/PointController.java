package com.shop.backend.point.controller;

import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.UserChecker;
import com.shop.backend.point.dto.PointRequest;
import com.shop.backend.point.dto.PointResponse;
import com.shop.backend.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/points")
public class PointController {

    private final PointService pointService;
    private final UserChecker userChecker;

    @GetMapping
    public ResponseEntity<PointResponse> myPoints(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer page) {
        if (!userChecker.isUser(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pointService.getPointHistory(
                        id,
                        PointRequest.builder()
                                .type(type)
                                .page(page)
                                .build()));
    }
}
