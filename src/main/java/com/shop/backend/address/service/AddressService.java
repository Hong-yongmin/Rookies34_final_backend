package com.shop.backend.address.service;

import com.shop.backend.address.dto.AddressCreateRequest;
import com.shop.backend.address.dto.AddressResponse;
import com.shop.backend.address.dto.AddressUpdateRequest;
import com.shop.backend.address.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    // 추가
    public AddressResponse addAddress(AddressCreateRequest req) {
        boolean success = addressRepository.addAddress(req);
        return AddressResponse.builder().code(success ? "ok" : "no").build();
    }

    // 수정
    public AddressResponse updateAddress(Integer addressId, AddressUpdateRequest req) {
        boolean success = addressRepository.updateAddress(addressId, req);
        return AddressResponse.builder().code(success ? "ok" : "no").build();
    }

    // 삭제
    public AddressResponse deleteAddress(Integer addressId) {
        boolean success = addressRepository.deleteAddress(addressId);
        return AddressResponse.builder().code(success ? "ok" : "no").build();
    }

}
