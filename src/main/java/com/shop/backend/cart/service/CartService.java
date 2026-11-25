package com.shop.backend.cart.service;

import com.shop.backend.cart.dto.*;
import com.shop.backend.cart.repository.CartRepositroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepositroy cartRepositroy;

    public Boolean addItem(String userId, CartItemsRequest req) {
        List<Integer> cartId = cartRepositroy.findCartId(userId);
        if (cartId.isEmpty()) {
            cartRepositroy.createCart(userId);
            cartId = cartRepositroy.findCartId(userId);
        }
        return cartRepositroy.addItem(cartId.get(0), req.getProduct_id(), req.getQuantity());
    }

    public CartResponse getCart(String userId) {
        CartResponse res = new CartResponse();
        res.setItems(cartRepositroy.findItems(userId));
        res.setNum(res.getItems().size());
        res.setCode("ok");

        List<Map<String, Object>> shipping = cartRepositroy.findShipping(userId);
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
        return cartRepositroy.updateQuantity(cartItemId, quantity);
    }

    public Boolean deleteItems(List<Integer> cartItemIds) {
        return cartRepositroy.deleteItems(cartItemIds);
    }

    public Boolean updateShipping(String userId, CartShippingAddressRequest req) {
        if (cartRepositroy.findCartId(userId).isEmpty()) cartRepositroy.createCart(userId);
        return cartRepositroy.updateShipping(userId, req.getAddress_id());
    }
}