package org.com.iot.iotbackend.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.com.iot.iotbackend.config.CorsConfig;
import org.com.iot.iotbackend.dto.common.CommonResponse;
import org.com.iot.iotbackend.dto.common.MetaData;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// TODO(참고용) : 스프링시큐리티 미적용으로 인한 JwtAuthentication 필터 작동을 위해 FilterConfig 설정추가
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final CorsConfig corsConfig;

    private final JwtTokenProvider jwtTokenProvider;
    private List<String> excludedPaths;

    public JwtAuthenticationFilter(CorsConfig corsConfig, JwtTokenProvider jwtTokenProvider) {
        this.corsConfig = corsConfig;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // "/api/auth" API 는 제외하도록 설정
        if (excludedPaths != null && isExcludedPath(request.getRequestURI())) {
            chain.doFilter(request, response); // 필터 건너뛰기
            return;
        }

        // JWT 토큰 처리 로직
        Cookie[] cookies = request.getCookies();
        String token = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("Authorization")) {
                    token = cookie.getValue();
                }
            }
        }

        // TODO : JWT 가 없거나 유효하지 않은 경우 로그인 후 사용 경고 구현
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden

            // CORS 관련 헤더 추가
            response.setHeader("Access-Control-Allow-Origin", corsConfig.getUrl());    // 배포 시 URL 설정 잘되는지 확인 필요
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
            response.setHeader("Access-Control-Allow-Credentials", "true");


            // 응답 타입 설정 (JSON 형식)
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            MetaData metaData = MetaData.ofError(HttpServletResponse.SC_FORBIDDEN, "로그인 후 이용가능합니다.");
            CommonResponse commonResponse = new CommonResponse(metaData);

            // 객체를 JSON으로 직렬화
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(commonResponse);
            response.getWriter().write(jsonResponse);
            response.flushBuffer();
            return;
        }


        if (token != null && jwtTokenProvider.validateToken(token)) {
            request.setAttribute("email", jwtTokenProvider.getAuthentication(token).getName());
        }

        chain.doFilter(request, response); // 필터 체인 계속 진행
    }

    // 설정된 경로가 제외 목록에 포함되어 있는지 확인
    private boolean isExcludedPath(String requestUri) {
        return excludedPaths.stream().anyMatch(requestUri::startsWith);
    }

    // 설정된 제외 경로들 초기화
    public void setExcludedPaths(List<String> excludedPaths) {
        this.excludedPaths = excludedPaths;
    }

}


