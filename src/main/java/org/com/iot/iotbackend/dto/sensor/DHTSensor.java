package org.com.iot.iotbackend.dto.sensor;

import lombok.Data;
import org.com.iot.iotbackend.common.enumtype.SensorType;
import org.com.iot.iotbackend.common.enumtype.UnitType;
import org.com.iot.iotbackend.entity.sensor.SensorData;
import org.com.iot.iotbackend.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
    온습도센서
    입력 : 온도, 습도
 */
@Data
public class DHTSensor {
    private String sensorId;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private LocalDateTime measured_at;

    public SensorData toTemperatureEntity(User user) {
        SensorData sensorData = SensorData.builder()
                .sensorId(this.sensorId)
                .sensorType(SensorType.DHT_TEMPERATURE.getValue())
                .measuredValue(this.temperature)
                .measuredAt(this.measured_at)
                .unit(UnitType.TEMPERATURE.getValue())
                .user(user)
                .build();
        return sensorData;
    }

    public SensorData toHumidityEntity(User user) {
        SensorData sensorData = SensorData.builder()
                .sensorId(this.sensorId)
                .sensorType(SensorType.DHT_HUMIDITY.getValue())
                .measuredValue(this.humidity)
                .measuredAt(this.measured_at)
                .unit(UnitType.HUMIDITY.getValue())
                .user(user)
                .build();
        return sensorData;
    }
}
