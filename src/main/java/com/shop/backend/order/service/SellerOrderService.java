package com.shop.backend.order.service;

import com.shop.backend.order.dto.*;
import com.shop.backend.order.repository.SellerOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerOrderService {
    private final SellerOrderRepository sellerOrderRepository;

    private OrderResponse ok(){ return OrderResponse.builder().code("ok").build(); }
    private OrderResponse no(){ return OrderResponse.builder().code("no").build(); }

    public SellerOrderListResponse getSellerOrders(String id, SellerOrderListRequest req){
        List<SellerOrder> orders = sellerOrderRepository.getSellerOrders(id, req);

        return SellerOrderListResponse.builder()
                .code("ok")
                .num(orders.size())
                .orders(orders)
                .build();
    }

    public OrderResponse updateShipment(String id, Long order_item_id, ShipmentRequest req){
        boolean hasTracking = req.getTracking_no() != null && !req.getTracking_no().isBlank();
        boolean hasCourier = req.getCourier() != null && !req.getCourier().isBlank();
        if (hasTracking != hasCourier){ // 송장 입력 시 택배사·송장번호 둘 다 필요
            return no();
        }
        if (sellerOrderRepository.updateShipment(id, order_item_id, req)){
            return ok();
        }
        return no();
    }

    public OrderResponse cancelOrderItem(String id, Long order_item_id, SellerCancelRequest req){
        String reason = req.getReason();
        if (!"OUT_OF_STOCK".equals(reason) && !"ETC".equals(reason)){
            return no();
        }
        if (sellerOrderRepository.cancelOrderItem(id, order_item_id, reason)){
            return ok();
        }
        return no();
    }
}
