package com.shop.backend.member.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerSignupRequest {
    private String login_id;
    private String password;
    private String manager_name;
    private String email;
    private String phone;
    private String business_reg_no;
    private String business_name;
    private String cert_file_path;  // 클라이언트 입력값 아님. WAS에 저장한 파일 경로를 서버에서 설정
    private MultipartFile business_cert_file;   // 사업자등록증 파일 (multipart)
    private String bank;
    private String account_no;
    private String account_holder;
    private Boolean agree_terms;
}
