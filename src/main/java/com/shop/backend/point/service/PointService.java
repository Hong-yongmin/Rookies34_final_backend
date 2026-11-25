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
                .balance(pointRepository.getBalance(id)) // 잔액은 user.point 값 사용
                .num(histories.size())
                .histories(histories).build();
    }
}
