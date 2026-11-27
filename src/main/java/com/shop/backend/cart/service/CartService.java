package com.shop.backend.cart.service;

import com.shop.backend.cart.dto.*;
import com.shop.backend.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    public Boolean addItem(String userId, CartItemsRequest req) {
        List<Integer> cartId = cartRepository.findCartId(userId);
        if (cartId.isEmpty()) {
            cartRepository.createCart(userId);
            cartId = cartRepository.findCartId(userId);
        }
        return cartRepository.addItem(cartId.get(0), req.getProduct_id(), req.getQuantity());
    }

    public CartResponse getCart(String userId) {
        CartResponse res = new CartResponse();
        res.setItems(cartRepository.findItems(userId));
        res.setNum(res.getItems().size());
        res.setCode("ok");

        List<Map<String, Object>> shipping = cartRepository.findShipping(userId);
        if (!shipping.isEmpty()) {
            Map<String, Object> s = shipping.get(0);
            res.setAddress_id((Integer) s.get("address_id"));
            res.setZipcode((String) s.get("zipcode"));
            res.setAddress((String) s.get("address"));
            res.setAddress_detail((String) s.get("address_detail"));
        }
        return res;
    }

    public Boolean updateQuantity(Integer cartItemId, Integer quantity) {
        return cartRepository.updateQuantity(cartItemId, quantity);
    }

    public Boolean deleteItems(List<Integer> cartItemIds) {
        return cartRepository.deleteItems(cartItemIds);
    }

    public Boolean updateShipping(String userId, CartShippingAddressRequest req) {
        if (cartRepository.findCartId(userId).isEmpty()) cartRepository.createCart(userId);
        return cartRepository.updateShipping(userId, req.getAddress_id());
    }
}