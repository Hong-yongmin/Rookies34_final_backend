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

    private final UserClaimRepository userClaimRepository;
    private final FileUploader fileUploader;

    private DefaultResponse ok(){ return DefaultResponse.builder().code("ok").build(); }
    private DefaultResponse no(){ return DefaultResponse.builder().code("no").build(); }

    @Transactional
    public DefaultResponse requestClaim(ClaimRequest req, Integer order_id, String user_id){
        String type;
        for(Integer order_item_id : req.getOrder_item_ids()) {
            type = req.getRequest_type();
            Integer claim_id;
            if((Objects.equals(type, "CANCEL") || Objects.equals(type, "EXCHANGE")) // 취소, 환불은 PAID일 때 가능
                    && Objects.equals(userClaimRepository.getStatus(order_item_id), "PAID")) {
                claim_id = userClaimRepository.requestClaim(req, order_item_id, user_id);
            }
            else if( Objects.equals(type, "RETURN")  // 반품은 COMPLETED일 때 가능
                    && Objects.equals(userClaimRepository.getStatus(order_item_id), "COMPLETED")){
                claim_id = userClaimRepository.requestClaim(req, order_item_id, user_id);
            }
            else return no();

            if(!Objects.equals(userClaimRepository.getPaymentMethod(order_id), "CARD")){ // card 결제가 아니면 현금 결제로 간주
                if(!userClaimRepository.addBankInfo(claim_id,
                        req.getRefund_bank(), req.getRefund_account_no(), req.getRefund_account_holder())) return no();
            }

            if(!userClaimRepository.updateOrderItemStatus(order_item_id)) return no();

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
