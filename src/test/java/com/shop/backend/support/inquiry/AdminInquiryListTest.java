package com.shop.backend.support.inquiry;

import com.shop.backend.global.security.AdminChecker;
import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.support.inquiry.controller.AdminInquiryController;
import com.shop.backend.support.inquiry.entity.Inquiry;
import com.shop.backend.support.inquiry.entity.InquiryCategory;
import com.shop.backend.support.inquiry.repository.InquiryRepository;
import com.shop.backend.support.inquiry.service.InquiryService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminInquiryListTest {

    @Test
    void checksPermissionAndReturnsTheRequestedPage() throws Exception {
        InquiryRepository repository = mock(InquiryRepository.class);
        AdminChecker adminChecker = mock(AdminChecker.class);
        when(adminChecker.isAdmin("admin")).thenReturn(true);
        var service = new InquiryService(repository, null, null, null);
        var mvc = MockMvcBuilders.standaloneSetup(new AdminInquiryController(service, adminChecker)).build();
        String url = "/api/admin/inquiries";

        mvc.perform(get(url)).andExpect(status().isUnauthorized());
        mvc.perform(get(url).requestAttr(JwtAuthenticationFilter.MEMBER_ID, "member"))
                .andExpect(status().isForbidden());
        mvc.perform(get(url).param("page", "0").requestAttr(JwtAuthenticationFilter.MEMBER_ID, "admin"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(repository);

        Inquiry inquiry = new Inquiry();
        inquiry.setInquiry_id(42);
        inquiry.setUser_id("member");
        inquiry.setCategory(InquiryCategory.values()[0]);
        inquiry.setTitle("Order question");
        inquiry.setContent("Please check my order");
        inquiry.setAttachment_url("stored-file.png");
        inquiry.setCreated_at(LocalDateTime.of(2026, 11, 27, 12, 0));
        var page = PageRequest.of(1, 10);
        when(repository.findAllForAdmin(page)).thenReturn(new PageImpl<>(List.of(inquiry), page, 11));

        mvc.perform(get(url).param("page", "2").requestAttr(JwtAuthenticationFilter.MEMBER_ID, "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ok"))
                .andExpect(jsonPath("$.num").value(11))
                .andExpect(jsonPath("$.inquiries[0].inquiry_id").value(42))
                .andExpect(jsonPath("$.inquiries[0].user_id").value("member"))
                .andExpect(jsonPath("$.inquiries[0].is_answered").value(false))
                .andExpect(jsonPath("$.inquiries[0].has_attachment").value(true))
                .andExpect(jsonPath("$.inquiries[0].attachment_url").doesNotExist());
    }
}
