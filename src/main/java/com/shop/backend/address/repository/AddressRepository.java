package com.shop.backend.address.repository;

import com.shop.backend.address.dto.*;
import com.shop.backend.global.common.CommonFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AddressRepository {
    private final JdbcTemplate jdbcTemplate;
    private final CommonFunction cf;

    public List<Map<String, Object>> findMyAddresses(String loginId) {
        return jdbcTemplate.queryForList(
                "SELECT * FROM address WHERE user_id=" + cf.q(loginId) + " ORDER BY is_default DESC");
    }

    public Boolean addAddress(String loginId, AddressCreateRequest req) {
        String sql = "INSERT INTO address (user_id, recipient_name, recipient_phone, zipcode, address, address_detail, is_default) VALUES ("
                + cf.q(loginId) + "," + cf.q(req.getRecipient_name()) + "," + cf.q(req.getRecipient_phone()) + ","
                + cf.q(req.getZipcode()) + "," + cf.q(req.getAddress()) + "," + cf.q(req.getAddress_detail()) + ","
                + Boolean.TRUE.equals(req.getIs_default()) + ")";
        return jdbcTemplate.update(sql) > 0;
    }

    public Boolean updateAddress(Integer addressId, AddressUpdateRequest req) {
        String sql = "UPDATE address SET recipient_name=" + cf.q(req.getRecipient_name())
                + ", recipient_phone=" + cf.q(req.getRecipient_phone())
                + ", zipcode=" + cf.q(req.getZipcode())
                + ", address=" + cf.q(req.getAddress())
                + ", address_detail=" + cf.q(req.getAddress_detail())
                + (Boolean.TRUE.equals(req.getIs_default()) ? ", is_default=true" : "")
                + " WHERE address_id=" + addressId;
        return jdbcTemplate.update(sql) > 0;
    }

    public Boolean deleteAddress(Integer addressId) {
        return jdbcTemplate.update("DELETE FROM address WHERE address_id=" + addressId) > 0;
    }

    public void clearDefault(String loginId) {
        jdbcTemplate.update("UPDATE address SET is_default=false WHERE user_id=" + cf.q(loginId));
    }

    public int countAddresses(String loginId) {
        Integer n = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM address WHERE user_id=" + cf.q(loginId), Integer.class);
        return n == null ? 0 : n;
    }

    public Boolean isDefault(String loginId, Integer addressId) {
        // 본인 소유면 기본 여부(true/false), 소유가 아니거나 없으면 null
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT is_default FROM address WHERE address_id=" + addressId + " AND user_id=" + cf.q(loginId));
        if (rows.isEmpty()) return null;
        Object v = rows.get(0).get("is_default");
        return v != null && (v instanceof Number ? ((Number) v).intValue() != 0 : Boolean.parseBoolean(v.toString()));
    }

    public void promoteDefault(String loginId) {
        // 남은 배송지 중 가장 오래된 것을 기본으로 승격 (기본 배송지 삭제 후 호출)
        jdbcTemplate.update("UPDATE address SET is_default=true WHERE user_id=" + cf.q(loginId)
                + " ORDER BY address_id LIMIT 1");
    }

    public void clearCartAddress(String loginId, Integer addressId) {
        // 장바구니에서 선택 중인 배송지면 선택 해제 (cart → address FK 때문에 삭제 전 호출)
        jdbcTemplate.update("UPDATE cart SET address_id=NULL WHERE address_id=" + addressId
                + " AND user_id=" + cf.q(loginId));
    }
}
