package com.shop.backend.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 제외
public class Claim {
    private Integer claim_id;
    private Integer order_id;
    private Integer order_item_id;
    private String user_id;
    private String request_type;
    private String reason_type;
    private String reason_detail;
    private String status;
    private List<MultipartFile> photo_files;
    private List<String> file_paths;
    private String refund_bank;
    private String refund_account_no;
    private String refund_account_holder;
    private String created_at;
}
