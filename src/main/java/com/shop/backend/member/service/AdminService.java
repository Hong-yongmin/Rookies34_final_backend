package com.shop.backend.member.service;

import com.shop.backend.member.dto.*;
import com.shop.backend.member.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;

    public AdminMemberListResponse getMembers(String keyword, String role, String status, Integer page){
        List<AdminMemberInfo> users = new ArrayList<>();
        if (role == null || role.equals("BUYER")){ // role이 없으면 구매자·판매자 모두 조회
            users.addAll(adminRepository.getUsersInfo(keyword, status, page));
        }
        if (role == null || role.equals("SELLER")){
            users.addAll(adminRepository.getSellersInfo(keyword, status, page));
        }

        return AdminMemberListResponse.builder()
                .code("ok")
                .num(users.size())
                .users(users)
                .build();
    }

    public MemberResponse changeUserStatus(String id, UserStatusRequest req){
        if (adminRepository.changeUserStatus(id, req)){
            return MemberResponse.builder()
                    .code("ok")
                    .build();
        }
        return MemberResponse.builder()
                .code("no")
                .build();
    }

    public MemberResponse changeSellerStatus(String id, SellerApprovalRequest req){
        if (adminRepository.changeSellerStatus(id, req)){
            return MemberResponse.builder()
                    .code("ok")
                    .build();
        }
        return MemberResponse.builder()
                .code("no")
                .build();
    }
}
