package com.shop.backend.global.common;

import org.springframework.stereotype.Component;

@Component
public class CommonFunction {
    public String q(String v) {
        return (v == null || v.isBlank()) ? "NULL" : "'" + v + "'";
    }
}
