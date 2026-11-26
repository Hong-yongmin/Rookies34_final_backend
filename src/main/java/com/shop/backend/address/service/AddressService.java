package com.shop.backend.address.service;

import com.shop.backend.address.dto.*;
import com.shop.backend.address.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    // 추가
    public AddressResponse addAddress(AddressCreateRequest req, String id) {
        boolean success = addressRepository.addAddress(req, id);
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

    // 조회
    public AddressesResponse getAddresses(String id){
        return AddressesResponse.builder()
                .code("ok")
                .addresses(addressRepository.getAddresses(id))
                .build();
    }

}
