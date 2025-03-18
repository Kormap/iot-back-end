package org.com.iot.iotbackend.dto.common;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class MetaData {
    private int status;
    private String message;

    public static MetaData of(String message) {
        return MetaData.builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .build();
    }

    public static MetaData ofError(int status, String message) {
        return MetaData.builder()
                .status(status)
                .message(message)
                .build();
    }
}
