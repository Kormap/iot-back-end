package org.com.iot.iotbackend.dto.sensor;

import lombok.Getter;

/*
    토양수분(포텐시오미터) 센서
    입력 : 수분량
 */
@Getter
public class SoilMoistureSensor {
    private float soilMoisture;
}
