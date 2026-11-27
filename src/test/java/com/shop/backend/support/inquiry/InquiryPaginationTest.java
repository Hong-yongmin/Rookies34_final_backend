package com.shop.backend.support.inquiry;

import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.support.inquiry.controller.InquiryController;
import com.shop.backend.support.inquiry.repository.InquiryRepository;
import com.shop.backend.support.inquiry.service.InquiryAttachmentLinkService;
import com.shop.backend.support.inquiry.service.InquiryService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InquiryPaginationTest {

    @ParameterizedTest
    @CsvSource({
            "/api/inquiries, 0",
            "/api/inquiries?page=0, 0",
            "/api/inquiries?page=1, 0",
            "/api/inquiries?page=2, 1"
    })
    void convertsPageToRepositoryIndexOnce(String url, int pageIndex) throws Exception {
        InquiryRepository repository = mock(InquiryRepository.class);
        InquiryAttachmentLinkService links = new InquiryAttachmentLinkService();
        InquiryService service = new InquiryService(repository, null, links, null);
        var mvc = MockMvcBuilders.standaloneSetup(new InquiryController(service, links)).build();

        mvc.perform(get(url).requestAttr(JwtAuthenticationFilter.MEMBER_ID, "owner"))
                .andExpect(status().isOk());

        verify(repository).findInquiriesByUserId("owner", PageRequest.of(pageIndex, 10));
    }
}
