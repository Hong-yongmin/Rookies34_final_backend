package com.shop.backend.order.service;

import com.shop.backend.order.dto.AdminOrder;
import com.shop.backend.order.dto.AdminOrderListRequest;
import com.shop.backend.order.dto.AdminOrderListResponse;
import com.shop.backend.order.repository.AdminOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminOrderService {
    private final AdminOrderRepository adminOrderRepository;

    public AdminOrderListResponse getOrders(AdminOrderListRequest req){
        List<AdminOrder> orders = adminOrderRepository.getOrders(req);

        return AdminOrderListResponse.builder()
                .code("ok")
                .num(orders.size())
                .orders(orders)
                .build();
    }
}
