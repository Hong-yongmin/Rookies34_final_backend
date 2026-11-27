package com.shop.backend.address.controller;

import com.shop.backend.address.dto.*;
import com.shop.backend.address.service.AddressService;
import com.shop.backend.global.security.UserChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/addresses")
public class AddressController {
    private final AddressService addressService;
    private final UserChecker userChecker;

    private static final Map<String, String> NO = Map.of("code", "no");
    private static final Map<String, String> OK = Map.of("code", "ok");

    @GetMapping
    public Object getMyAddresses(@RequestAttribute(name = "memberid", required = false) String userId) {
        if (!userChecker.isUser(userId)) return NO;
        return addressService.getMyAddresses(userId);
    }

    @PostMapping
    public Map<String, String> addAddress(@RequestAttribute(name = "memberid", required = false) String userId,
                                          @RequestBody AddressCreateRequest req) {
        if (!userChecker.isUser(userId)) return NO;
        if ("ok".equals(addressService.addAddress(userId, req))) return OK;
        return NO;
    }

    @PutMapping("/{addressId}")
    public Map<String, String> updateAddress(@RequestAttribute(name = "memberid", required = false) String userId,
                                             @PathVariable("addressId") Integer addressId, @RequestBody AddressUpdateRequest req) {
        if (!userChecker.isUser(userId)) return NO;
        if ("ok".equals(addressService.updateAddress(userId, addressId, req))) return OK;
        return NO;
    }

    @DeleteMapping("/{addressId}")
    public Map<String, String> deleteAddress(@RequestAttribute(name = "memberid", required = false) String userId,
                                             @PathVariable("addressId") Integer addressId) {
        if (!userChecker.isUser(userId)) return NO;
        if ("ok".equals(addressService.deleteAddress(addressId))) return OK;
        return NO;
    }
}
