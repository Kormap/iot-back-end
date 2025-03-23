package org.com.iot.iotbackend.config;

import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.com.iot.iotbackend.common.JwtAuthenticationFilter;
import org.com.iot.iotbackend.common.JwtTokenProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

// TODO(참고용) : 스프링시큐리티 미적용으로 인한 FilterConfig 설정추가
@Configuration
public class FilterConfig {
    private final FrontCorsUrlConfig frontCorsUrlConfig;
    private final JwtTokenProvider jwtTokenProvider;
    private final String appEnv;

    public FilterConfig(FrontCorsUrlConfig frontCorsUrlConfig,
                        JwtTokenProvider jwtTokenProvider,
                        @Value("${app.env}") String appEnv) {
        this.frontCorsUrlConfig = frontCorsUrlConfig;
        this.jwtTokenProvider = jwtTokenProvider;
        this.appEnv = appEnv;
    }

    @Bean(name = "customJwtAuthenticationFilter")
    public FilterRegistrationBean<Filter> jwtAuthenticationFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(frontCorsUrlConfig, jwtTokenProvider, appEnv);

        // /api/auth/*, sensor data 경로를 제외한 경로만 필터 적용
        List<String> excludedPaths =
                Arrays.asList(
                        "/api/auth/users/verify-email/send",
                        "/api/auth/users/verify-email",
                        "/api/auth/signup",
                        "/api/auth/login",
                        "/api/auth/logout",
                        "/api/sensor/data"
                );
        filter.setExcludedPaths(excludedPaths);

        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/api/*"); // /api/로 시작하는 모든 요청에 대해 필터 적용
        registrationBean.setOrder(1); // 필터 순서 지정 (낮을수록 먼저 실행)

        return registrationBean;
    }
}
