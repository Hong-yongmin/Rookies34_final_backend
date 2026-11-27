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
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserClaimService {
    private static final Set<String> REQUEST_TYPES = Set.of("CANCEL", "RETURN", "EXCHANGE");
    private static final Set<String> REASON_TYPES = Set.of("CHANGE_MIND", "DEFECT", "WRONG_ITEM", "DELAY", "ETC");

    private final UserClaimRepository userClaimRepository;
    private final FileUploader fileUploader;

    private DefaultResponse ok(){ return DefaultResponse.builder().code("ok").build(); }
    private DefaultResponse no(){ return DefaultResponse.builder().code("no").build(); }

    @Transactional
    public DefaultResponse requestClaim(ClaimRequest req, Integer order_id, Integer orderItemId, String user_id){
        if (req.getRequest_type() == null || !REQUEST_TYPES.contains(req.getRequest_type())
                || req.getReason_type() == null || !REASON_TYPES.contains(req.getReason_type())) {
            return no();
        }

        List<Integer> requestedItems = orderItemId == null ? req.getOrder_item_ids() : List.of(orderItemId);
        if (requestedItems == null || requestedItems.isEmpty()) return no();
        List<Integer> itemIds = requestedItems.stream().distinct().toList();

        String paymentMethod = userClaimRepository.getPaymentMethod(order_id, user_id);
        if (paymentMethod == null || !userClaimRepository.getOrderItemIds(order_id).containsAll(itemIds)) {
            return no();
        }

        Integer claim_id = userClaimRepository.requestClaim(req, order_id, user_id);
        if (claim_id == null) return rollback();

        if (!"CARD".equals(paymentMethod)) {
            if(!userClaimRepository.addBankInfo(claim_id,
                    req.getRefund_bank(), req.getRefund_account_no(), req.getRefund_account_holder())) return rollback();
        }
        if (!userClaimRepository.addClaimItems(claim_id, order_id, itemIds)) return rollback();

        List<MultipartFile> files = req.getPhoto_files();
        if (files != null) {
            int index = 0;
            for (MultipartFile file : files.stream().filter(file -> !file.isEmpty()).limit(3).toList()) {
                String name = file.getOriginalFilename();
                if (name == null || name.isBlank() || name.contains("/") || name.contains("\\")
                        || ".".equals(name) || "..".equals(name) || name.contains(":")) return rollback();

                String path;
                try {
                    path = fileUploader.save(file, "claim-image/%d/%d".formatted(claim_id, index++));
                } catch (IOException e) {
                    return rollback();
                }
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_ROLLED_BACK) fileUploader.delete(path);
                    }
                });
                if (!userClaimRepository.addClaimImage(claim_id, path)) return rollback();
            }
        }
        return ok();
    }

    private DefaultResponse rollback() {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return no();
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
