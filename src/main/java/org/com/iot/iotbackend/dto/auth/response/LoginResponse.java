package org.com.iot.iotbackend.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.com.iot.iotbackend.common.JwtTokens;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    private JwtTokens jwtTokens;
}
