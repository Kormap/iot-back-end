package org.com.iot.iotbackend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class EmailRequest {
    @Schema(description = "이메일 주소", example = "example@gmail.com")
    @Email(message = "Invalid email format")
    private String email;
}
