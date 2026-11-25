package com.shop.backend.address.repository;

import com.shop.backend.address.dto.AddressCreateRequest;
import com.shop.backend.address.dto.AddressUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AddressRepository {
    private final JdbcTemplate jdbcTemplate;
    //  배송지 추가 성공 시 true 반환
    public Boolean addAddress(AddressCreateRequest req) {
        String sql = "INSERT INTO address (user_id, recipient_name, recipient_phone, zipcode, address, address_detail, is_default) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        int rows = jdbcTemplate.update(sql, req.getUser_id(),
                req.getRecipient_name(), req.getRecipient_phone(), req.getZipcode(),
                req.getAddress(), req.getAddress_detail(), Boolean.TRUE.equals(req.getIs_default()));
        return rows > 0;
    }

    //  배송지 수정 성공 시 true 반환 (해당 배송지가 없으면 false)
    public Boolean updateAddress(Integer addressId, AddressUpdateRequest req) {
        String sql = "UPDATE address SET recipient_name = ?, recipient_phone = ?, zipcode = ?, "
                + "address = ?, address_detail = ?, is_default = ? WHERE address_id = ?";
        int rows = jdbcTemplate.update(sql,
                req.getRecipient_name(), req.getRecipient_phone(), req.getZipcode(),
                req.getAddress(), req.getAddress_detail(), Boolean.TRUE.equals(req.getIs_default()),
                addressId);
        return rows > 0;
    }

    //  배송지 삭제 성공 시 true 반환 (해당 배송지가 없으면 false)
    public Boolean deleteAddress(Integer addressId) {
        String sql = "DELETE FROM address WHERE address_id = ?";
        int rows = jdbcTemplate.update(sql, addressId);
        return rows > 0;
    }
}
