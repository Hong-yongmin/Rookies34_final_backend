package com.shop.backend.support.inquiry;

import com.jayway.jsonpath.JsonPath;
import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.support.inquiry.controller.InquiryController;
import com.shop.backend.support.inquiry.entity.Inquiry;
import com.shop.backend.support.inquiry.entity.InquiryCategory;
import com.shop.backend.support.inquiry.repository.InquiryRepository;
import com.shop.backend.support.inquiry.service.InquiryAttachmentLinkService;
import com.shop.backend.support.inquiry.service.InquiryFileService;
import com.shop.backend.support.inquiry.service.InquiryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InquiryAttachmentDownloadTest {

    @TempDir
    Path directory;

    @Test
    void ownerCanOpenDetailLinkInBrowserWithoutAuthorizationHeader() throws Exception {
        Path attachment = Files.writeString(directory.resolve("attachment.txt"), "attachment");
        Inquiry inquiry = new Inquiry();
        inquiry.setInquiry_id(42);
        inquiry.setUser_id("owner");
        inquiry.setCategory(InquiryCategory.ETC);
        inquiry.setCreated_at(LocalDateTime.now());
        inquiry.setAttachment_url("attachment.txt");

        InquiryRepository repository = mock(InquiryRepository.class);
        when(repository.findById(42)).thenReturn(Optional.of(inquiry));
        InquiryFileService files = mock(InquiryFileService.class);
        when(files.getFilePath("attachment.txt")).thenReturn(attachment);
        InquiryAttachmentLinkService links = new InquiryAttachmentLinkService();
        InquiryService service = new InquiryService(repository, files, links, null);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new InquiryController(service, links)).build();

        String detail = mvc.perform(get("/api/inquiries/42")
                        .requestAttr(JwtAuthenticationFilter.MEMBER_ID, "owner"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String link = JsonPath.read(detail, "$.attachment");

        mvc.perform(get(URI.create(link)))
                .andExpect(status().isOk())
                .andExpect(content().bytes(Files.readAllBytes(attachment)));
        mvc.perform(get(URI.create(link.replace("/42/", "/43/"))))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/inquiries/42/attachment"))
                .andExpect(status().isUnauthorized());
    }
}
