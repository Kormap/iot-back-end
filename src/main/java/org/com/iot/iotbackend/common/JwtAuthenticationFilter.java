package org.com.iot.iotbackend.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.com.iot.iotbackend.config.FrontCorsUrlConfig;
import org.com.iot.iotbackend.dto.common.CommonResponse;
import org.com.iot.iotbackend.dto.common.MetaData;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// TODO(참고용) : 스프링시큐리티 미적용으로 인한 JwtAuthentication 필터 작동을 위해 FilterConfig 설정
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final String APP_ENV;
    private final FrontCorsUrlConfig frontCorsUrlConfig;
    private final JwtTokenProvider jwtTokenProvider;
    private List<String> excludedPaths;

    public JwtAuthenticationFilter(FrontCorsUrlConfig frontCorsUrlConfig, JwtTokenProvider jwtTokenProvider, String appEnv) {
        this.frontCorsUrlConfig = frontCorsUrlConfig;
        this.jwtTokenProvider = jwtTokenProvider;
        this.APP_ENV = appEnv;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // OPTIONS HTTP 메소드 필터 제외
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(request, response); // 필터 건너뛰기
            return;
        }
        // CORS 관련 헤더 추가
        response.setHeader("Access-Control-Allow-Origin", frontCorsUrlConfig.getUrl());    // 배포 시 URL 설정 잘되는지 확인 필요
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // "/api/auth" API 는 제외하도록 설정
        if (excludedPaths != null && isExcludedPath(request.getRequestURI())) {
            chain.doFilter(request, response); // 필터 건너뛰기
            return;
        }

        // JWT 토큰 처리 로직
        Cookie[] cookies = request.getCookies();
        String accessToken = null;
        String refreshToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("Authorization")) {
                    accessToken = cookie.getValue();
                }
                if (cookie.getName().equals("Refresh-Token")) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        // 액세스 토큰이 없는 경우
        if (accessToken == null) {
            // 리프레쉬 토큰이 있는지 확인
            if (refreshToken != null && jwtTokenProvider.validateRefreshToken(refreshToken)) {
                // 리프레쉬 토큰이 유효하면 새 액세스 토큰 발급
                try {
                    String newAccessToken = jwtTokenProvider.reissueAccessToken(refreshToken);

                    // 새 액세스 토큰을 쿠키에 설정
                    setAccessTokenCookie(newAccessToken, response);

                    // 새 액세스 토큰으로 인증 정보 설정
                    request.setAttribute("email", jwtTokenProvider.getAuthentication(newAccessToken).getName());
                    chain.doFilter(request, response); // 필터 체인 계속 진행
                    return;
                } catch (Exception e) {
                    // 리프레쉬 토큰 처리 중 오류 발생
                    sendUnauthorizedResponse(response, "리프레쉬 토큰 처리 중 오류가 발생했습니다: " + e.getMessage());
                    return;
                }
            } else {
                // 리프레쉬 토큰도 없거나 유효하지 않은 경우
                sendUnauthorizedResponse(response, "로그인 후 이용가능합니다.");
                return;
            }
        }

        // 액세스 토큰이 있지만 만료된 경우
        if (!jwtTokenProvider.validateToken(accessToken)) {
            // 액세스 토큰이 만료되었는지 확인
            if (jwtTokenProvider.isTokenExpired(accessToken)) {
                // 리프레쉬 토큰이 있고 유효한지 확인
                if (refreshToken != null && jwtTokenProvider.validateRefreshToken(refreshToken)) {
                    try {
                        // 새 액세스 토큰 발급
                        String newAccessToken = jwtTokenProvider.reissueAccessToken(refreshToken);

                        // 새 액세스 토큰을 쿠키에 설정
                        setAccessTokenCookie(newAccessToken, response);

                        // 새 액세스 토큰으로 인증 정보 설정
                        request.setAttribute("email", jwtTokenProvider.getAuthentication(newAccessToken).getName());
                        chain.doFilter(request, response); // 필터 체인 계속 진행
                        return;
                    } catch (Exception e) {
                        // 리프레쉬 토큰 처리 중 오류 발생
                        sendUnauthorizedResponse(response, "리프레쉬 토큰 처리 중 오류가 발생했습니다.");
                        return;
                    }
                } else {
                    // 리프레쉬 토큰이 없거나 유효하지 않은 경우
                    sendUnauthorizedResponse(response, "세션이 만료되었습니다. 다시 로그인해주세요.");
                    return;
                }
            } else {
                // 액세스 토큰이 유효하지 않은 경우 (만료가 아닌 다른 이유)
                sendUnauthorizedResponse(response, "유효하지 않은 인증 정보입니다.");
                return;
            }
        }

        // 액세스 토큰이 유효한 경우
        request.setAttribute("email", jwtTokenProvider.getAuthentication(accessToken).getName());
        chain.doFilter(request, response); // 필터 체인 계속 진행
    }

    // 인증 실패 응답을 보내는 헬퍼 메서드
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 UNAUTHORIZED
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        MetaData metaData = MetaData.ofError(HttpServletResponse.SC_UNAUTHORIZED, message);
        CommonResponse commonResponse = new CommonResponse(metaData);

        // 객체를 JSON으로 직렬화
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(commonResponse);
        response.getWriter().write(jsonResponse);
        response.flushBuffer();
    }

    private void setAccessTokenCookie(String accessToken, HttpServletResponse response) {
        // 환경에 따라 Secure 설정, 배포(https) : true, 로컬(http) : false
        boolean isProd = APP_ENV.equals("docker");
        // 환경에 따라 SameSite 설정 배포(https) : None, 로컬(http) : SameSite None 설정 시, Secure(true) 환경에서만 가능
        String sameSiteConfig = APP_ENV.equals("docker") ? "None" : "Strict";

        Cookie accessTokenCookie = new Cookie("Authorization", accessToken);
        accessTokenCookie.setHttpOnly(true); // HTTP-Only 설정: JavaScript에서 접근 불가
        accessTokenCookie.setSecure(isProd);  // Secure 설정
        accessTokenCookie.setPath("/");    // 모든 경로에서 유효
        accessTokenCookie.setMaxAge(15 * 60); // 유효 기간: 15분 (Access 토큰의 유효 기간과 동일)
        accessTokenCookie.setAttribute("SameSite", sameSiteConfig);
        response.addCookie(accessTokenCookie);
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


