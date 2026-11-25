package com.shop.backend.order.controller;

import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.SellerChecker;
import com.shop.backend.order.dto.*;
import com.shop.backend.order.service.SellerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller")
public class SellerOrderController {

    private final SellerOrderService sellerOrderService;
    private final SellerChecker sellerChecker;

    @GetMapping("/orders")
    public ResponseEntity<SellerOrderListResponse> sellerOrders(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @RequestParam(required = false) String date_from,
            @RequestParam(required = false) String date_to,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page){
        if (!sellerChecker.isSeller(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(sellerOrderService.getSellerOrders(
                        id,
                        SellerOrderListRequest.builder()
                                .date_from(date_from)
                                .date_to(date_to)
                                .status(status)
                                .page(page)
                                .build()));
    }

    @PatchMapping("/order-items/{orderItemId}/shipment")
    public ResponseEntity<OrderResponse> updateShipment(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @PathVariable("orderItemId") Long order_item_id,
            @RequestBody ShipmentRequest req){
        if (!sellerChecker.isSeller(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(sellerOrderService.updateShipment(id, order_item_id, req));
    }

    @PostMapping("/order-items/{orderItemId}/cancellation")
    public ResponseEntity<OrderResponse> cancelOrderItem(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @PathVariable("orderItemId") Long order_item_id,
            @RequestBody SellerCancelRequest req){
        if (!sellerChecker.isSeller(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(sellerOrderService.cancelOrderItem(id, order_item_id, req));
    }
}
