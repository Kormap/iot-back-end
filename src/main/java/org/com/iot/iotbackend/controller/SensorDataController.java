package org.com.iot.iotbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.com.iot.iotbackend.dto.SensorDataDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/api/sensor")
public class SensorDataController {

    @Operation(summary = "센서데이터 수신", description = "센서세이터를 수신합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "sensor data post : SUCCESS"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @PostMapping("/data")
    public ResponseEntity<String> getSensorData(SensorDataDTO sensorDataDTO) {
        log.info("Temperature={} Humidity={}", sensorDataDTO.getTemperature(), sensorDataDTO.getHumidity());
        return ResponseEntity.ok("SUCCESS");
    }
}
