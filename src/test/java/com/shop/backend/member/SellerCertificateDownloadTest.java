package com.shop.backend.member;

import com.shop.backend.global.common.FileDownloader;
import com.shop.backend.global.security.AdminChecker;
import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.SellerChecker;
import com.shop.backend.member.controller.SellerController;
import com.shop.backend.member.dto.SellerInfo;
import com.shop.backend.member.repository.SellerRepository;
import com.shop.backend.member.service.SellerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SellerCertificateDownloadTest {

    @TempDir Path uploadRoot;

    @Test
    void profileReturnsAnEncodedUrlThatDownloadsTheCertificate() throws Exception {
        String path = "cert/사업자 등록증+1.pdf";
        byte[] certificate = {1, 2, 3};
        Files.createDirectory(uploadRoot.resolve("cert"));
        Files.write(uploadRoot.resolve(path), certificate);

        SellerRepository repository = mock(SellerRepository.class);
        when(repository.getSellerInfo("seller")).thenAnswer(call ->
                SellerInfo.builder().cert_file_url(path).build());
        SellerChecker sellerChecker = mock(SellerChecker.class);
        when(sellerChecker.isSeller("seller")).thenReturn(true);
        FileDownloader downloader = new FileDownloader();
        ReflectionTestUtils.setField(downloader, "uploadDir", uploadRoot.toString());
        var service = new SellerService(repository, null, null, null);
        var mvc = MockMvcBuilders.standaloneSetup(new SellerController(
                service, sellerChecker, mock(AdminChecker.class), downloader)).build();

        String url = "/api/seller/cert-file?path=cert%2F%EC%82%AC%EC%97%85%EC%9E%90%20%EB%93%B1%EB%A1%9D%EC%A6%9D%2B1.pdf";
        mvc.perform(get("/api/seller/profile").requestAttr(JwtAuthenticationFilter.MEMBER_ID, "seller"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cert_download_url").value(url));
        verify(repository).getSellerInfo("seller");

        mvc.perform(get(URI.create(url)).requestAttr(JwtAuthenticationFilter.MEMBER_ID, "seller"))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
                .andExpect(content().bytes(certificate));
    }
}
