package com.shop.backend.address.service;

import com.shop.backend.address.dto.*;
import com.shop.backend.address.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    public List<Map<String, Object>> getMyAddresses(String userId) {
        return addressRepository.findMyAddresses(userId);
    }

    @Transactional
    public Boolean addAddress(String userId, AddressCreateRequest req) {
        // 첫 배송지는 무조건 기본, 기본 지정 시 기존 기본은 해제 → 기본 배송지는 항상 1개 유지
        boolean makeDefault = Boolean.TRUE.equals(req.getIs_default())
                || addressRepository.countAddresses(userId) == 0;
        if (makeDefault) {
            addressRepository.clearDefault(userId);
            req.setIs_default(true);
        }
        return addressRepository.addAddress(userId, req);
    }

    @Transactional
    public Boolean updateAddress(String userId, Integer addressId, AddressUpdateRequest req) {
        if (Boolean.TRUE.equals(req.getIs_default())) addressRepository.clearDefault(userId);
        return addressRepository.updateAddress(addressId, req);
    }

    @Transactional
    public Boolean deleteAddress(String userId, Integer addressId) {
        // 본인 소유 배송지만 삭제 (소유 아니면 실패)
        Boolean wasDefault = addressRepository.isDefault(userId, addressId);
        if (wasDefault == null) return false;
        addressRepository.clearCartAddress(userId, addressId); // 장바구니 선택 배송지면 먼저 해제
        if (!addressRepository.deleteAddress(addressId)) return false;
        // 기본 배송지를 삭제했다면 남은 배송지 중 하나를 기본으로 승격 → 기본 배송지 1개 유지
        if (wasDefault) addressRepository.promoteDefault(userId);
        return true;
    }
} 
