package org.com.iot.iotbackend.dto;

import lombok.Getter;

@Getter
public class SensorDataDTO {
    private DHTSensor dhtSensor;
    private SoilMoistureSensor soilMoistureSensor;
}
