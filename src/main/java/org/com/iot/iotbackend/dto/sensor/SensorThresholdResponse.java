package org.com.iot.iotbackend.dto.sensor;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SensorThresholdResponse {
    private String sensorType;
    private BigDecimal minThreshold;
    private BigDecimal maxThreshold;
}
