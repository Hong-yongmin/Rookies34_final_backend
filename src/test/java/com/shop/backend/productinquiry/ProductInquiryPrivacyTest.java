package com.shop.backend.productinquiry;

import com.shop.backend.productinquiry.dto.MyInquiryDetailResponse;
import com.shop.backend.productinquiry.dto.ProductInquiryResponse;
import com.shop.backend.productinquiry.repository.ProductInquiryRepository;
import com.shop.backend.productinquiry.service.ProductInquiryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.server.ResponseStatusException;

import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProductInquiryPrivacyTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"other", "owner"})
    void onlyOwnerCanReadPrivateListFields(String viewer) throws Exception {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        ResultSet row = mock(ResultSet.class);
        when(row.getBoolean("is_secret")).thenReturn(true);
        when(row.getString("user_id")).thenReturn("owner");
        when(row.getString("title")).thenReturn("Private title");
        when(row.getString("content")).thenReturn("Private content");
        when(row.getString("answer")).thenReturn("Private answer");
        when(jdbc.query(anyString(), org.mockito.ArgumentMatchers.<RowMapper<ProductInquiryResponse>>any(),
                eq(1L), eq(0L))).thenAnswer(call -> {
            RowMapper<ProductInquiryResponse> mapper = call.getArgument(1);
            return List.of(mapper.mapRow(row, 0));
        });

        ProductInquiryResponse inquiry = new ProductInquiryRepository(jdbc).getByProduct(1L, 1, viewer).get(0);
        assertTrue(inquiry.is_secret());
        assertTrue(inquiry.is_answered());
        if ("owner".equals(viewer)) {
            assertEquals("Private content", inquiry.getContent());
            assertEquals("Private answer", inquiry.getAnswer());
        } else {
            assertEquals("접근 권한이 없습니다.", inquiry.getTitle());
            assertEquals("-", inquiry.getWriter());
            assertNull(inquiry.getContent());
            assertNull(inquiry.getAnswer());
        }
    }

    @Test
    void personalDetailLookupIsScopedToTheViewer() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        ProductInquiryService service = new ProductInquiryService(new ProductInquiryRepository(jdbc));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.getDetail(1L, "other"));
        assertEquals(404, exception.getStatusCode().value());
        verify(jdbc).query(contains("i.inquiry_id = ? AND i.user_id = ?"),
                org.mockito.ArgumentMatchers.<RowMapper<MyInquiryDetailResponse>>any(), eq(1L), eq("other"));
    }
}
