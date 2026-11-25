package com.shop.backend.address.controller;

import com.shop.backend.address.dto.AddressCreateRequest;
import com.shop.backend.address.dto.AddressResponse;
import com.shop.backend.address.dto.AddressUpdateRequest;
import com.shop.backend.address.dto.AddressesResponse;
import com.shop.backend.address.service.AddressService;
import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.UserChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AddressController {

    private final AddressService addressService;
    private final UserChecker userChecker;

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

    // 배송지 조회
    @GetMapping("/me/addresses")
    public ResponseEntity<AddressesResponse> getAddresses(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id){
        if(!userChecker.isUser(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
        }
        return ResponseEntity.ok(addressService.getAddresses(id));
    }
}
