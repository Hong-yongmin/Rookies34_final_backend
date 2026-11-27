package com.shop.backend.member.controller;

import com.shop.backend.global.common.FileDownloader;
import com.shop.backend.global.security.AdminChecker;
import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.SellerChecker;
import com.shop.backend.member.dto.*;
import com.shop.backend.member.service.SellerCertificateLinkService;
import com.shop.backend.member.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SellerController {

    private final SellerService sellerService;
    private final SellerChecker sellerChecker;
    private final AdminChecker adminChecker;
    private final FileDownloader fileDownloader;
    private final SellerCertificateLinkService certificateLinks;

    @PostMapping("/sellers") // multipart/form-data로 수신, business_cert_file은 파일
    public ResponseEntity<MemberResponse> signup(@ModelAttribute SellerSignupRequest req){
        return ResponseEntity.status(HttpStatus.OK).body(sellerService.signupSeller(req));
    }

    @GetMapping("/seller/profile")
    public ResponseEntity<SellerInfoResponse> myInfo(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id){
        if (!sellerChecker.isSeller(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(sellerService.getSellerInfo(id));
    }

    @PutMapping("/seller/profile")
    public ResponseEntity<MemberResponse> changeMyInfo(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @RequestBody SellerProfileRequest req){
        if (!sellerChecker.isSeller(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(sellerService.changeSellerInfo(id, req));
    }

    @PutMapping("/seller/password")
    public ResponseEntity<MemberResponse> changeMyPassword(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @RequestBody PasswordChangeRequest req){
        if (!sellerChecker.isSeller(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(sellerService.changeSellerPassword(id, req));
    }

    @GetMapping("/seller/cert-file") // 사업자등록증 다운로드
    public ResponseEntity<Resource> downloadCertFile(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @RequestParam(name = "download_token", required = false) String downloadToken,
            @RequestParam(name = "path", required = false) String path){
        if (downloadToken != null) {
            String owner = certificateLinks.getSellerId(downloadToken);
            if (id != null && !id.equals(owner)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다운로드 권한이 없습니다.");
            }
            return fileDownloader.download(sellerService.getCertificatePath(owner));
        }
        if (id != null && sellerChecker.isSeller(id)) {
            return fileDownloader.download(sellerService.getCertificatePath(id));
        }
        if (id != null && adminChecker.isAdmin(id)) {
            return fileDownloader.download(path);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "권한 필요");
    }
}
