package com.shop.backend.member.service;

import com.shop.backend.global.common.FileUploader;
import com.shop.backend.global.security.JwtUtil;
import com.shop.backend.member.dto.*;
import com.shop.backend.member.repository.SellerRepository;
import com.shop.backend.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SellerService {
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final FileUploader fileUploader;
    private final JwtUtil jwtUtil;
    private final SellerCertificateLinkService certificateLinks;

    private static final String CERT_DIR = "cert"; // 사업자등록증 저장 폴더 ({uploadDir}/cert)

    private MemberResponse ok(){ return MemberResponse.builder().code("ok").build(); }
    private MemberResponse no(){ return MemberResponse.builder().code("no").build(); }

    private boolean isBlank(String v){ return v == null || v.isBlank(); }

    public MemberResponse checkId(String id){
        if( sellerRepository.checkSellerId(id)){
            return ok();
        }
        return no();
    }

    public MemberResponse signupSeller(SellerSignupRequest req){
        // 필수 항목 확인
        if (isBlank(req.getLogin_id()) || isBlank(req.getPassword()) || isBlank(req.getManager_name())
                || isBlank(req.getEmail()) || isBlank(req.getPhone())
                || isBlank(req.getBusiness_reg_no()) || isBlank(req.getBusiness_name())
                || req.getBusiness_cert_file() == null || req.getBusiness_cert_file().isEmpty()){
            return no();
        }
        if (!Boolean.TRUE.equals(req.getAgree_terms())){ // 판매자 약관 동의 필수
            return no();
        }
        // 로그인 API가 공용이므로 구매자·판매자 아이디 모두 중복 확인
        if (!sellerRepository.checkSellerId(req.getLogin_id()) || !userRepository.checkUserId(req.getLogin_id())){
            return no();
        }

        String certPath;
        try {
            certPath = fileUploader.save(req.getBusiness_cert_file(), CERT_DIR);
        } catch (IOException e) { // 저장 실패
            return no();
        }

        // DB엔 저장 경로 저장
        req.setCert_file_path(certPath);
        if (sellerRepository.signupSeller(req)){
            return ok();
        }
        fileUploader.delete(certPath); // DB 저장 실패 시 저장한 파일 정리
        return no();
    }

    public SellerInfoResponse getSellerInfo(String id){
        SellerInfo info = sellerRepository.getSellerInfo(id);
        if (!isBlank(info.getCert_file_url())) {
            info.setCert_download_url(certificateLinks.createLink(id));
        }

        return SellerInfoResponse.builder()
                .code("ok")
                .sellerInfo(info)
                .build();
    }

    public String getCertificatePath(String id) {
        return sellerRepository.getSellerInfo(id).getCert_file_url();
    }

    public MemberResponse changeSellerInfo(String id, SellerProfileRequest req){
        if (isBlank(req.getBusiness_name()) || isBlank(req.getPhone()) || isBlank(req.getBank())
                || isBlank(req.getAccount_no()) || isBlank(req.getAccount_holder())){
            return no();
        }
        // 요청에 없는 항목은 기존 값 유지
        SellerInfo before = sellerRepository.getSellerInfo(id);

        if (sellerRepository.changeSellerInfo(id, SellerInfo.builder()
                .manager_name(before.getManager_name())
                .email(before.getEmail())
                .phone(req.getPhone())
                .business_reg_no(before.getBusiness_reg_no())
                .business_name(req.getBusiness_name())
                .cert_file_url(before.getCert_file_url())
                .bank(req.getBank())
                .account_no(req.getAccount_no())
                .account_holder(req.getAccount_holder())
                .build())){
            return ok();
        }
        return no();
    }

    public MemberResponse changeSellerPassword(String id, PasswordChangeRequest req){
        if (isBlank(req.getCurrent_password()) || isBlank(req.getNew_password())){
            return no();
        }
        if (!sellerRepository.checkSellerPassword(id, req.getCurrent_password())){
            return no();
        }
        if (sellerRepository.changeSellerPassword(id, req.getNew_password())){
            return ok();
        }
        return no();
    }
}
