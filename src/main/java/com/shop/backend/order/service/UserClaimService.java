package com.shop.backend.order.service;

import com.shop.backend.global.common.FileUploader;
import com.shop.backend.order.dto.Claim;
import com.shop.backend.order.dto.ClaimRequest;
import com.shop.backend.order.dto.ClaimResponse;
import com.shop.backend.order.dto.DefaultResponse;
import com.shop.backend.order.repository.UserClaimRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserClaimService {

    private UserClaimRepository userClaimRepository;
    private final FileUploader fileUploader;

    private DefaultResponse ok(){ return DefaultResponse.builder().code("ok").build(); }
    private DefaultResponse no(){ return DefaultResponse.builder().code("no").build(); }

    @Transactional
    public DefaultResponse requestClaim(ClaimRequest req, Integer order_id, String user_id){
        Integer claim_id = userClaimRepository.requestClaim(req, order_id, user_id);
        if(claim_id == null) return no();

        if(!Objects.equals(userClaimRepository.getPaymentMethod(order_id), "CARD")){ // card 결제가 아니면 현금 결제로 간주
            if(!userClaimRepository.addBankInfo(claim_id,
                    req.getRefund_bank(), req.getRefund_account_no(), req.getRefund_account_holder())) return no();
        }
        if(!userClaimRepository.addClaimItem(claim_id, order_id)) return no();

        String dir = "%s/claim-image".formatted(user_id);
        String path;
        List<MultipartFile> files = req.getPhoto_files();
        if(files != null){ // 사진 업로드 시
            int i = 0;
            for(MultipartFile file : files){
                if(i >= 3) break; // 사진은 3장까지만 저장
                i++;
                try {
                    path = fileUploader.save(file, dir);
                }catch (IOException e){ // 저장 실패
                    return no();
                }
                if(!userClaimRepository.addClaimImage(claim_id, path)) return no();
            }
        }
        return ok();
    }

    public ClaimResponse getClaims(String user_id, Integer page){
        List<Claim> result = userClaimRepository.getClaims(user_id, page);
        return ClaimResponse.builder()
                .code("ok")
                .num(result.size())
                .claims(result)
                .build();
    }
}
