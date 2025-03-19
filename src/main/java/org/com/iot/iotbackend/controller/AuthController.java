package org.com.iot.iotbackend.controller;

import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.iot.iotbackend.common.JwtTokenProvider;
import org.com.iot.iotbackend.config.CorsConfig;
import org.com.iot.iotbackend.dto.auth.request.EmailRequest;
import org.com.iot.iotbackend.dto.auth.request.LoginRequest;
import org.com.iot.iotbackend.dto.auth.request.SignupRequest;
import org.com.iot.iotbackend.dto.auth.request.VerifyEmailRequest;
import org.com.iot.iotbackend.dto.auth.response.LoginResponse;
import org.com.iot.iotbackend.dto.common.MetaData;
import org.com.iot.iotbackend.dto.common.CommonResponse;
import org.com.iot.iotbackend.service.AuthService;
import org.com.iot.iotbackend.service.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Value("${app.env}")
    private String APP_ENV;

    private final MailService mailService;
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    Logger logger = Logger.getLogger(AuthController.class.getName());

    @Operation(
            summary = "본인인증 메일 전송 API",
            description = "회원가입 시 본인인증 메일 전송을 합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Succeeded"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @PostMapping("/users/verify-email/send")
    public ResponseEntity<CommonResponse> verify(@RequestBody @Valid EmailRequest request) {
        if (StringUtils.isBlank(request.getEmail())) {
            throw new IllegalArgumentException("이메일 입력 후 본인인증을 진행해주세요.");
        }

        String email = request.getEmail();
        // 인증코드 생성
        String verifyCode = jwtTokenProvider.generateVerifyCode();
        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateVerifyAccessToken(email, verifyCode);

        mailService.callSendVerifyEmail(email, verifyCode);

        MetaData metaData = MetaData.of("OK: Succeeded");
        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", accessToken);

        CommonResponse response = new CommonResponse(metaData, data);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "본인인증 메일 검증 API", description = "회원가입 시 본인인증 코드를 검증합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Succeeded"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @PostMapping("/users/verify-email")
    public ResponseEntity<CommonResponse> verifyEmail(@RequestBody VerifyEmailRequest verifyEmailRequest) {

        jwtTokenProvider.validateVerifyAccessToken(verifyEmailRequest.getAccessToken(), verifyEmailRequest.getEmail(), verifyEmailRequest.getVerifyCode());

        MetaData metaData = MetaData.of("OK: Succeeded");
        Map<String, Object> data = new HashMap<>();
        data.put("hasEmailAuth", true);

        CommonResponse response = new CommonResponse(metaData, data);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원가입 API", description = "회원가입을 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Succeeded"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse> signup(@RequestBody @Valid SignupRequest request) {
        authService.signup(request);

        MetaData metaData = MetaData.of("OK: Succeeded");
        CommonResponse response = new CommonResponse<>(metaData);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그인 API", description = "로그인 요청을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Succeeded"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponse>> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse httpResponse
    ) {
        LoginResponse data = authService.login(request);

        // 토큰을 HTTP-Only 쿠키에 저장
        setHttpOnlyCookie(data, httpResponse);

        // 클라이언트로 반환 시 JWT 토큰 제거
        data.setJwtTokens(null);

        MetaData metaData = MetaData.of("OK: Succeeded");
        CommonResponse response = new CommonResponse<>(metaData, data);
        return ResponseEntity.ok(response);
    }

    // 로그아웃 참고 코드
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // AccessToken 삭제
        ResponseCookie accessTokenCookie = ResponseCookie.from("Authorization", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Strict")
                .maxAge(0)  // 쿠키 즉시 만료
                .build();

        // RefreshToken 삭제
        ResponseCookie refreshTokenCookie = ResponseCookie.from("Refresh-Token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Strict")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        logger.info("test API");
        return ResponseEntity.ok("success");
    }


    public void setHttpOnlyCookie(LoginResponse data, HttpServletResponse httpResponse) {
        boolean isProd = APP_ENV.equals("docker");   // 환경에 따라 Secure 설정, https : true, http : false
        Cookie accessTokenCookie = new Cookie("Authorization", data.getJwtTokens().getAccessToken());
        accessTokenCookie.setHttpOnly(true); // HTTP-Only 설정: JavaScript에서 접근 불가
        accessTokenCookie.setSecure(isProd);  // Secure 설정
        accessTokenCookie.setPath("/");    // 모든 경로에서 유효
        accessTokenCookie.setMaxAge(15 * 60); // 유효 기간: 15분 (Access 토큰의 유효 기간과 동일)
        accessTokenCookie.setAttribute("SameSite", "Strict");
        httpResponse.addCookie(accessTokenCookie);

        // Refresh 토큰을 HTTP-Only 쿠키에 저장
        Cookie refreshTokenCookie = new Cookie("Refresh-Token", data.getJwtTokens().getRefreshToken());
        refreshTokenCookie.setHttpOnly(true); // HTTP-Only 설정: JavaScript에서 접근 불가
        refreshTokenCookie.setSecure(isProd);  // Secure 설정
        refreshTokenCookie.setPath("/");    // 모든 경로에서 유효
        refreshTokenCookie.setMaxAge(24 * 60 * 60); // 유효 기간: 1일 (Refresh 토큰의 유효 기간과 동일)
        refreshTokenCookie.setAttribute("SameSite", "Strict");
        httpResponse.addCookie(refreshTokenCookie);
    }
}
