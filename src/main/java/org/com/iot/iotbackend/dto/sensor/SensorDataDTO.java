package org.com.iot.iotbackend.dto.sensor;

import lombok.Data;
import org.com.iot.iotbackend.dto.user.request.UserDTO;

@Data
public class SensorDataDTO {
    private UserDTO user;
    private DHTSensor dhtSensor;
    private SoilMoistureSensor soilMoistureSensor;
}
