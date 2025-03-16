package org.com.iot.iotbackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.com.iot.iotbackend.dto.SensorDataDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/api/sensor")
public class SensorDataController {

    @PostMapping("/data")
    public void getSensorData(SensorDataDTO sensorDataDTO) {
        log.info("Temperature={} Humidity={}", sensorDataDTO.getTemperature(), sensorDataDTO.getHumidity());
    }
}
