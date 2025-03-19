package org.com.iot.iotbackend.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginRequest {
    @Schema(description = "이메일 주소", example = "example@gmail.com")
    @Email(message = "유효하지 않은 이메일 형식입니다.")
    private String email;

    @Schema(description = "비밀번호", example = "!qwer1234")
    private String password;
}
