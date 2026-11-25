package com.shop.backend.point.service;

import com.shop.backend.point.dto.History;
import com.shop.backend.point.dto.PointRequest;
import com.shop.backend.point.dto.PointResponse;
import com.shop.backend.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;

    public PointResponse getPointHistory(String id, PointRequest req){
        List<History> histories = pointRepository.getHistories(id, req);

        return PointResponse.builder()
                .code("ok")
                .balance(getBalance(id))
                .num(histories.size())
                .histories(histories).build();
    }

    private int getBalance(String id){ // point_history의 내역을 모두 확인하여 현재 잔액 계산
        List<History> histories = pointRepository.getHistories(id, null);
        int sum = 0;
        for(History h : histories){
            sum += h.getAmount();
        }
        return sum;
    }
}
