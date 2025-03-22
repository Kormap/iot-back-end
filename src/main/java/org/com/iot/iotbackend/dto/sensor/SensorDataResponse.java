package org.com.iot.iotbackend.dto.sensor;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SensorDataResponse {
    private LocalDateTime measuredAt; // 측정일시
    private BigDecimal measuredValue; // 측정값
}
