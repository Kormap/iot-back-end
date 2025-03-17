package org.com.iot.iotbackend.dto.sensor;

import lombok.Getter;

/*
    온습도센서
    입력 : 온도, 습도
 */
@Getter
public class DHTSensor {
    private float temperature;
    private float humidity;
}
