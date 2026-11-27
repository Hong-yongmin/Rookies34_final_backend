package com.shop.backend.order.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimRequest {
    private List<Integer> order_item_ids;
    private String request_type;
    private String reason_type;
    private String reason_detail;
    private List<MultipartFile> photo_files;
    private List<String> file_paths;
    private String refund_bank;
    private String refund_account_no;
    private String refund_account_holder;
}
