package org.com.iot.iotbackend.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    private Long id;

    @NotBlank(message = "휴대폰 번호는 필수입니다.")
    @Pattern(regexp = "^010[0-9]{7,8}$", message = "올바른 형식의 휴대폰 번호가 아닙니다.")
    private String phoneNumber;
}
