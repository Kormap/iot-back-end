package org.com.iot.iotbackend.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EmailRequest {
    @Schema(description = "이메일 주소", example = "example@gmail.com")
    @Email(message = "유효하지 않은 이메일 형식입니다.")
    private String email;
}
