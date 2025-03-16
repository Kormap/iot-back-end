package org.com.iot.iotbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SensorDataDTO {
    private float temperature;
    private float humidity;
}
