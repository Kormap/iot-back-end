package org.com.iot.iotbackend.dto.sensor;

import lombok.Data;
import org.com.iot.iotbackend.common.enumtype.SensorType;
import org.com.iot.iotbackend.common.enumtype.UnitType;
import org.com.iot.iotbackend.entity.sensor.SensorData;
import org.com.iot.iotbackend.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
    토양수분(포텐시오미터) 센서
    입력 : 수분량
 */
@Data
public class SoilMoistureSensor {
    private String sensorId;
    private BigDecimal soilMoisture;
    private LocalDateTime measured_at;

    public SensorData toEntity(User user) {
        SensorData sensorData = SensorData.builder()
                .sensorId(this.sensorId)
                .sensorType(SensorType.SOIL_MOISTURE.getValue())
                .measuredValue(this.soilMoisture)
                .measuredAt(this.measured_at)
                .unit(UnitType.MOISTURE.getValue())
                .user(user)
                .build();
        return sensorData;
    }
}
