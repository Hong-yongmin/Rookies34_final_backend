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
        if (req.getProduct_id() == null || !cartRepository.existsProduct(req.getProduct_id())) return false;

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

        int totalPrice = 0;
        int totalDiscount = 0;
        for (CartResponse.item i : res.getItems()) {
            int salePrice = i.getPrice() * (100 - i.getDiscount_rate()) / 100;
            i.setSale_price(salePrice);
            totalPrice += i.getPrice() * i.getQuantity();
            totalDiscount += (i.getPrice() - salePrice) * i.getQuantity();
        }
        res.setTotal_price(totalPrice);
        res.setTotal_discount(totalDiscount);
        res.setFinal_price(totalPrice - totalDiscount);

        List<Map<String, Object>> selected = cartRepository.findSelectedAddress(userId);
        if (!selected.isEmpty()) {
            Map<String, Object> s = selected.get(0);
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
        return cartRepository.updateSelectedAddress(userId, req.getAddress_id());
    }
}