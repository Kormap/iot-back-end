package org.com.iot.iotbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SensorDataDTO {
    private String temperature;
    private String humidity;
}
