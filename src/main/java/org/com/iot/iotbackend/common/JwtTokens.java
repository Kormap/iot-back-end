package org.com.iot.iotbackend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtTokens {
    private String accessToken;
    private String refreshToken;
}
