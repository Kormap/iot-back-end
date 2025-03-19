package org.com.iot.iotbackend.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.com.iot.iotbackend.entity.User;

@Data
public class SignupRequest {
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]{1,20}@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "이메일 아이디는 최대 20자까지 입력 가능합니다.")
    @Size(max = 40, message = "이메일은 최대 40자까지 입력 가능합니다.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @Pattern(regexp = "^[가-힣]{1,10}$", message = "이름은 한글만 가능하며, 최대 10자까지 입력 가능합니다.")
    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,20}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함해 8~20자 사이여야 합니다."
    )
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String confirmPassword;

    @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "연락처는 숫자 10~11자리로만 입력 가능합니다.")
    @NotBlank(message = "연락처를 입력해주세요.")
    private String phoneNumber;

    public User toEntity() {
        return new User(
                this.email,
                this.name,
                this.password,
                this.phoneNumber
        );
    }
}
