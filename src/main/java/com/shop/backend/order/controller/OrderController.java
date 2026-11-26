package com.shop.backend.order.controller;

import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.UserChecker;
import com.shop.backend.order.dto.*;
import com.shop.backend.order.service.OrderService;
import com.shop.backend.order.service.UserClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserClaimService userClaimService;
    private final UserChecker userChecker;

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @RequestBody CheckoutRequest req){
        if (!userChecker.isUser(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(orderService.checkout(id, req));
    }

    @PostMapping
    public ResponseEntity<OrderCreateResponse> createOrder(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @RequestBody OrderCreateRequest req){
        if (!userChecker.isUser(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(orderService.createOrder(id, req));
    }

    @GetMapping
    public ResponseEntity<OrderListResponse> myOrders(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page){
        if (!userChecker.isUser(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.getOrders(
                        id,
                        OrderListRequest.builder()
                                .period(period)
                                .keyword(keyword)
                                .page(page)
                                .build()));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> orderDetail(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @PathVariable("orderId") Long order_id){
        if (!userChecker.isUser(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderDetail(id, order_id));
    }

    @PostMapping("/{orderId}/claims")
    public ResponseEntity<DefaultResponse> requestClaim(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @PathVariable("orderId") Integer order_id,
            @RequestBody ClaimRequest req){
        if (!userChecker.isUser(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
        }
        return ResponseEntity.ok(userClaimService.requestClaim(req, order_id, id));
    }
}
