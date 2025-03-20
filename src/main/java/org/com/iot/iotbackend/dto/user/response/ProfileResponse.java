package org.com.iot.iotbackend.dto.user.response;

import lombok.Data;

@Data
public class ProfileResponse {
    private String email;
    private String name;
    private String phoneNumber;
}
