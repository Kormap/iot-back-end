package org.com.iot.iotbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.iot.iotbackend.dto.auth.EmailRequest;
import org.com.iot.iotbackend.dto.auth.VerifyEmailRequest;
import org.com.iot.iotbackend.dto.common.SingleDataResponse;
import org.com.iot.iotbackend.service.AuthService;
import org.com.iot.iotbackend.service.MailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MailService mailService;
    private final AuthService authService;

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
    public ResponseEntity<SingleDataResponse> verify(@RequestBody @Valid EmailRequest request) {
        String email = request.getEmail();
        // 인증코드 생성
        String verifyCode = authService.generateVerifyCode();
        // JWT 토큰 생성
        String accessToken = authService.generateVerifyAccessToken(email, verifyCode);

        mailService.callSendVerifyEmail(email, verifyCode);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("status", HttpStatus.OK.value());
        metadata.put("message", "OK: Succeeded");

        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", "Bearer " + accessToken);

        SingleDataResponse response = new SingleDataResponse();
        response.setMetadata(metadata);
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "본인인증 메일 검증 API", description = "회원가입 시 본인인증 코드를 검증합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Succeeded"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @PostMapping("/users/verify-email")
    public ResponseEntity<SingleDataResponse> verifyEmail(@RequestBody VerifyEmailRequest verifyEmailRequest) {

        authService.validateVerifyAccessToken(verifyEmailRequest.getAccessToken(), verifyEmailRequest.getVerifyCode());

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("status", HttpStatus.OK.value());
        metadata.put("message", "OK: Succeeded");

        Map<String, Object> data = new HashMap<>();
        data.put("hasEmailAuth", true);

        SingleDataResponse response = new SingleDataResponse();
        response.setMetadata(metadata);
        response.setData(data);

        return ResponseEntity.ok(response);
    }
}
