package com.shop.backend.cart.repository;

import com.shop.backend.global.common.CommonFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.shop.backend.cart.dto.CartResponse;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CartRepositroy {
    private final JdbcTemplate jdbcTemplate;
    private final CommonFunction cf;

    public List<Integer> findCartId(String userId) {
        return jdbcTemplate.queryForList("SELECT cart_id FROM cart WHERE user_id=" + cf.q(userId), Integer.class);
    }

    public void createCart(String userId) {
        jdbcTemplate.update("INSERT INTO cart (user_id) VALUES (" + cf.q(userId) + ")");
    }

    public Boolean existsProduct(Integer productId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM product WHERE product_id=" + productId, Integer.class) > 0;
    }

    public Boolean addItem(Integer cartId, Integer productId, Integer quantity) {
        return jdbcTemplate.update("INSERT INTO cart_item (cart_id, product_id, quantity) VALUES ("
                + cartId + "," + productId + "," + quantity + ")") > 0;
    }

    public List<CartResponse.item> findItems(String userId) {
        return jdbcTemplate.query("SELECT ci.cart_item_id, ci.product_id, ci.quantity,"
                        + " p.name, p.price, p.discount_rate, p.thumbnail AS thumbnail"
                        + " FROM cart c"
                        + " JOIN cart_item ci ON c.cart_id = ci.cart_id"
                        + " JOIN product p ON ci.product_id = p.product_id"
                        + " WHERE c.user_id=" + cf.q(userId),
                new BeanPropertyRowMapper<>(CartResponse.item.class));
    }

    public Boolean updateQuantity(Integer cartItemId, Integer quantity) {
        return jdbcTemplate.update("UPDATE cart_item SET quantity=" + quantity + " WHERE cart_item_id=" + cartItemId) > 0;
    }

    public Boolean deleteItems(List<Integer> cartItemIds) {
        int rows = 0;
        for (Integer id : cartItemIds) rows += jdbcTemplate.update("DELETE FROM cart_item WHERE cart_item_id=" + id);
        return rows > 0;
    }

    public Boolean updateSelectedAddress(String userId, Integer addressId) {
        return jdbcTemplate.update("UPDATE cart SET address_id=" + addressId + " WHERE user_id=" + cf.q(userId)) > 0;
    }

    public List<Map<String, Object>> findSelectedAddress(String userId) {
        return jdbcTemplate.queryForList("SELECT a.address_id, a.zipcode, a.address, a.address_detail"
                + " FROM cart c JOIN address a ON c.address_id = a.address_id"
                + " WHERE c.user_id=" + cf.q(userId));
    }
}
