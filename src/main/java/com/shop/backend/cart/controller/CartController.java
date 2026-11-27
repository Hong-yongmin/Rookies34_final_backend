package com.shop.backend.cart.controller;

import com.shop.backend.cart.dto.*;
import com.shop.backend.cart.service.CartService;
import com.shop.backend.global.security.UserChecker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;
    private final UserChecker userChecker;

    private static final Map<String, String> NO = Map.of("code", "no");
    private static final Map<String, String> OK = Map.of("code", "ok");

    @GetMapping
    public Object getCart(@RequestAttribute(name = "memberid", required = false) String userId) {
        if (!userChecker.isUser(userId)) return NO;
        return cartService.getCart(userId);
    }

    @PostMapping("/items")
    public Map<String, String> addItem(@RequestAttribute(name = "memberid", required = false) String userId,
                                       @Valid @RequestBody CartItemsRequest req) {
        if (!userChecker.isUser(userId)) return NO;
        if (cartService.addItem(userId, req)) return OK;
        return NO;
    }

    @PatchMapping("/items/{cartItemId}")
    public Map<String, String> updateQuantity(@RequestAttribute(name = "memberid", required = false) String userId,
                                              @PathVariable Integer cartItemId, @Valid @RequestBody CartItemIdRequest req) {
        if (!userChecker.isUser(userId)) return NO;
        if (cartService.updateQuantity(cartItemId, req.getQuantity())) return OK;
        return NO;
    }

    @DeleteMapping("/items")
    public Map<String, String> deleteItems(@RequestAttribute(name = "memberid", required = false) String userId,
                                           @RequestBody CartItemDeleteRequest req) {
        if (!userChecker.isUser(userId)) return NO;
        if (cartService.deleteItems(req.getCart_item_ids())) return OK;
        return NO;
    }

    @PutMapping("/shipping-address")
    public Map<String, String> updateShipping(@RequestAttribute(name = "memberid", required = false) String userId,
                                              @RequestBody CartShippingAddressRequest req) {
        if (!userChecker.isUser(userId)) return NO;
        if (cartService.updateShipping(userId, req)) return OK;
        return NO;
    }
}