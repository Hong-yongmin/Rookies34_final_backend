package com.shop.backend.global.common;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommonFunction {
    public String q(String v) {
        return (v == null || v.isBlank()) ? "NULL" : "'" + v + "'";
    }

    public String join(List<?> list) { // IN (...) 조건용, [1, 2, 3] -> "1,2,3"
        return list.stream().map(String::valueOf).collect(Collectors.joining(","));
    }
}
