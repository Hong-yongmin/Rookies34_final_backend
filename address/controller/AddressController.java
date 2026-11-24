package com.shop.backend.address.controller;

import com.shop.backend.address.dto.AddressCreateRequest;
import com.shop.backend.address.dto.AddressResponse;
import com.shop.backend.address.dto.AddressUpdateRequest;
import com.shop.backend.address.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AddressController {
    private final AddressService addressService;

    // 배송지 추가
    @PostMapping("/me/addresses")
    public ResponseEntity<AddressResponse> addAddress(@RequestBody AddressCreateRequest req) {
        return ResponseEntity.ok(addressService.addAddress(req));
    }

    // 배송지 수정
    @PutMapping("/me/addresses/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable Integer addressId,
                                                         @RequestBody AddressUpdateRequest req) {
        return ResponseEntity.ok(addressService.updateAddress(addressId, req));
    }

    // 배송지 삭제
    @DeleteMapping("/me/addresses/{addressId}")
    public ResponseEntity<AddressResponse> deleteAddress(@PathVariable Integer addressId) {
        return ResponseEntity.ok(addressService.deleteAddress(addressId));
    }
}
