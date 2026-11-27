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
        if (Boolean.TRUE.equals(req.getIs_default())) addressRepository.clearDefault(userId);
        return addressRepository.addAddress(userId, req);
    }

    @Transactional
    public Boolean updateAddress(String userId, Integer addressId, AddressUpdateRequest req) {
        if (Boolean.TRUE.equals(req.getIs_default())) addressRepository.clearDefault(userId);
        return addressRepository.updateAddress(addressId, req);
    }

    public Boolean deleteAddress(Integer addressId) {
        return addressRepository.deleteAddress(addressId);
    }
} 
