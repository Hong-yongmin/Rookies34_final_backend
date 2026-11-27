package com.shop.backend.productinquiry;

import com.shop.backend.productinquiry.dto.ProductInquiryResponse;
import com.shop.backend.seller.inquiry.dto.SellerInquiryResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

class InquiryAnswerStatusJsonTest {

    private final JsonMapper mapper = JsonMapper.builder().build();

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void bothListsExposeAnswerStatusUnderTheApiField(boolean answered) {
        String answer = answered ? "Seller reply" : null;
        var product = ProductInquiryResponse.builder().is_answered(answered).answer(answer).build();
        var seller = SellerInquiryResponse.builder().is_answered(answered).answer(answer).build();

        for (Object item : new Object[]{product, seller}) {
            var json = mapper.readTree(mapper.writeValueAsString(item));
            assertTrue(json.path("is_answered").isBoolean());
            assertEquals(answered, json.path("is_answered").booleanValue());
            assertFalse(json.has("_answered"));
        }
    }
}
