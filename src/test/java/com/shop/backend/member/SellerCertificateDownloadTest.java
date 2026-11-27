package com.shop.backend.member;

import com.shop.backend.global.common.FileDownloader;
import com.shop.backend.global.security.AdminChecker;
import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.SellerChecker;
import com.shop.backend.member.controller.SellerController;
import com.shop.backend.member.dto.SellerInfo;
import com.shop.backend.member.repository.SellerRepository;
import com.shop.backend.member.service.SellerCertificateLinkService;
import com.shop.backend.member.service.SellerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.ClassPathResource;
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

    @ParameterizedTest
    @ValueSource(strings = {"cert/", "/backend-shop/uploads/products/"})
    void profileLinkDownloadsTheCertificateWithoutAnAuthorizationHeader(String prefix) throws Exception {
        String filename = "사업자 등록증+1.pdf";
        byte[] certificate = {1, 2, 3};
        Path directory = uploadRoot.resolve(prefix.equals("cert/") ? "cert" : "products");
        Files.createDirectory(directory);
        Files.write(directory.resolve(filename), certificate);
        var mvc = createMvc(prefix + filename);

        var profile = mvc.perform(get("/api/seller/profile").requestAttr(JwtAuthenticationFilter.MEMBER_ID, "seller"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"))
                .andReturn().getResponse().getContentAsString();
        String url = tools.jackson.databind.json.JsonMapper.builder().build()
                .readTree(profile).path("cert_download_url").stringValue();

        mvc.perform(get(URI.create(url)))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
                .andExpect(content().bytes(certificate));
        mvc.perform(get(URI.create(url)).requestAttr(JwtAuthenticationFilter.MEMBER_ID, "other"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/seller/cert-file").param("download_token", "invalid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void returnsTheBundledSampleWhenItIsMissingFromTheUploadDirectory() throws Exception {
        var mvc = createMvc("/dummy-images/cert/sample.png");
        mvc.perform(get("/api/seller/cert-file").requestAttr(JwtAuthenticationFilter.MEMBER_ID, "seller"))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
                .andExpect(content().bytes(new ClassPathResource("seller-certificates/sample.png").getContentAsByteArray()));
    }

    private org.springframework.test.web.servlet.MockMvc createMvc(String path) {
        SellerRepository repository = mock(SellerRepository.class);
        when(repository.getSellerInfo("seller")).thenAnswer(call ->
                SellerInfo.builder().cert_file_url(path).build());
        SellerChecker sellerChecker = mock(SellerChecker.class);
        when(sellerChecker.isSeller("seller")).thenReturn(true);
        FileDownloader downloader = new FileDownloader();
        ReflectionTestUtils.setField(downloader, "uploadDir", uploadRoot.toString());
        ReflectionTestUtils.setField(downloader, "productUploadDir", uploadRoot.resolve("products").toString());
        var links = new SellerCertificateLinkService();
        var service = new SellerService(repository, null, null, null, links);
        return MockMvcBuilders.standaloneSetup(new SellerController(
                service, sellerChecker, mock(AdminChecker.class), downloader, links)).build();
    }
}
