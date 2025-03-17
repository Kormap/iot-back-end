package org.com.iot.iotbackend.dto.sensor;

import lombok.Getter;
import org.com.iot.iotbackend.dto.user.UserDTO;

@Getter
public class SensorDataDTO {
    private UserDTO user;
    private DHTSensor dhtSensor;
    private SoilMoistureSensor soilMoistureSensor;
}
