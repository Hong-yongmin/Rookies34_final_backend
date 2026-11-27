package com.shop.backend.order;

import com.shop.backend.global.common.FileUploader;
import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.UserChecker;
import com.shop.backend.order.controller.UserClaimController;
import com.shop.backend.order.repository.UserClaimRepository;
import com.shop.backend.order.service.UserClaimService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserClaimRequestTest {

    @ParameterizedTest
    @CsvSource({
            "CANCEL, /api/orders/42/claims, 20",
            "RETURN, /api/orders/42/claims, 20",
            "EXCHANGE, /api/orders/42/claims/21, 21"
    })
    void savesOnlyTheRequestedOrderItem(String type, String url, int itemId) throws Exception {
        UserChecker users = mock(UserChecker.class);
        when(users.isUser("buyer")).thenReturn(true);
        UserClaimRepository claims = mock(UserClaimRepository.class);
        when(claims.getPaymentMethod(42, "buyer")).thenReturn("CARD");
        when(claims.getOrderItemIds(42)).thenReturn(List.of(20, 21, 22));
        when(claims.requestClaim(any(), eq(42), eq("buyer"))).thenReturn(50);
        when(claims.addClaimItems(50, 42, List.of(itemId))).thenReturn(true);
        var service = new UserClaimService(claims, mock(FileUploader.class));
        var mvc = MockMvcBuilders.standaloneSetup(new UserClaimController(users, service)).build();

        mvc.perform(multipart(url)
                        .requestAttr(JwtAuthenticationFilter.MEMBER_ID, "buyer")
                        .param("request_type", type)
                        .param("reason_type", "DEFECT")
                        .param("order_item_ids", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ok"));

        verify(claims).addClaimItems(50, 42, List.of(itemId));
    }
}
