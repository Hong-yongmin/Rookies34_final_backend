package com.shop.backend.order.controller;

import com.shop.backend.global.security.AdminChecker;
import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.order.dto.AdminOrderListRequest;
import com.shop.backend.order.dto.AdminOrderListResponse;
import com.shop.backend.order.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;
    private final AdminChecker adminChecker;

    @GetMapping
    public ResponseEntity<AdminOrderListResponse> orders(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @RequestParam(required = false) String date_from,
            @RequestParam(required = false) String date_to,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String seller_id,
            @RequestParam(required = false) Integer page){
        if (!adminChecker.isAdmin(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 권한 필요");
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminOrderService.getOrders(
                        AdminOrderListRequest.builder()
                                .date_from(date_from)
                                .date_to(date_to)
                                .status(status)
                                .seller_id(seller_id)
                                .page(page)
                                .build()));
    }
}
