package org.com.iot.iotbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.com.iot.iotbackend.dto.DHTSensor;
import org.com.iot.iotbackend.dto.SensorDataDTO;
import org.com.iot.iotbackend.dto.SoilMoistureSensor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/sensor")
public class SensorDataController {

    @Operation(summary = "센서데이터 수신", description = "센서세이터를 수신합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "sensor data post : SUCCESS"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @PostMapping("/data")
    public ResponseEntity<String> getSensorData(@RequestBody SensorDataDTO sensorData) {
        DHTSensor dhtSensor = sensorData.getDhtSensor();
        SoilMoistureSensor soilMoistureSensor = sensorData.getSoilMoistureSensor();

        log.info("Temperature={} Humidity={}", dhtSensor.getTemperature(), dhtSensor.getHumidity());
        log.info("SoilMoisture={}", soilMoistureSensor.getSoilMoisture());
        return ResponseEntity.ok("SUCCESS");
    }
}
