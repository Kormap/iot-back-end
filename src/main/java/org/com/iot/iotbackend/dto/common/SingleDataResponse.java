package org.com.iot.iotbackend.dto.common;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class SingleDataResponse {
    private Map<String, Object> metadata;
    private Map<String, Object> data;
}