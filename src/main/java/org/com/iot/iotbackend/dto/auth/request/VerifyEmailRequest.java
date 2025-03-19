package org.com.iot.iotbackend.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class VerifyEmailRequest {
    @Schema(description = "액세스 토큰", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
    String accessToken;

    @Schema(description = "이메일 주소", example = "example@gmail.com")
    @Email(message = "유효하지 않은 이메일 형식입니다.")
    private String email;

    @Schema(description = "검증 코드", example = "123456")
    String verifyCode;
}
