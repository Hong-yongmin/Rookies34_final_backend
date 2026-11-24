package com.shop.backend.global.config;

import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.JwtUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter(JwtUtil jwtUtil){
        FilterRegistrationBean<JwtAuthenticationFilter> bean = new FilterRegistrationBean<>(new JwtAuthenticationFilter(jwtUtil));
        bean.addUrlPatterns("/api/*");
        bean.setOrder(1);
        return bean;
    }
}
