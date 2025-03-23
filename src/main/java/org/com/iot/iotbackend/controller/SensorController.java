package org.com.iot.iotbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.iot.iotbackend.dto.common.CommonResponse;
import org.com.iot.iotbackend.dto.common.MetaData;
import org.com.iot.iotbackend.dto.sensor.*;
import org.com.iot.iotbackend.dto.user.request.UserDTO;
import org.com.iot.iotbackend.service.SensorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/sensor")
public class SensorController {
    private final SensorService sensorService;

    /*
    *   아두이노 <-> 백엔드 서버 통신
    */
    @Operation(summary = "센서데이터(아두이노센서) 수신 API", description = "센서 데이터를 수신합니다.(아두이노 <-> 백엔드)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "sensor data post : SUCCESS"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @PostMapping("/data")
    public ResponseEntity<String> getSensorData(@RequestBody SensorDataDTO sensorData) {
        UserDTO user = sensorData.getUser();
        DHTSensor dhtSensor = sensorData.getDhtSensor();
        SoilMoistureSensor soilMoistureSensor = sensorData.getSoilMoistureSensor();
        // TODO : 로그확인용 출력, 추후 제거
        log.info("User Email={}", user.getEmail());
        log.info("Temperature={}, Humidity={}, SoilMoisture={}", dhtSensor.getTemperature(), dhtSensor.getHumidity(), soilMoistureSensor.getSoilMoisture());

        sensorService.saveSensorData(sensorData);

        return ResponseEntity.ok("SUCCESS");
    }

    /*
     *   프론트엔드 <-> 백엔드 서버 통신
     */
    @Operation(summary = "센서 데이터 조회 API", description = "센서 데이터를 조회합니다.(프론트엔드 <-> 백엔드)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "sensor data post : SUCCESS"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @GetMapping("/users/{userId}/sensor-data")
    public ResponseEntity<CommonResponse<List<SensorDataResponse>>> getSensorsData(@PathVariable Long userId, @RequestParam String sensorType) {
        List<SensorDataResponse> dataList = sensorService.getSensorsData(userId, sensorType);

        MetaData metaData = MetaData.of("OK: Succeeded");
        CommonResponse response = new CommonResponse<>(metaData, dataList);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "센서 임계값(설정범위) 조회 API", description = "센서 임계값(설정범위)를 조회합니다.(프론트엔드 <-> 백엔드)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "sensor data post : SUCCESS"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @GetMapping("/users/{userId}/sensor-threshold")
    public ResponseEntity<CommonResponse<List<SensorThresholdResponse>>> getSensorsThreshold(@PathVariable Long userId) {
        List<SensorThresholdResponse> dataList = sensorService.getSensorsThreshold(userId);

        MetaData metaData = MetaData.of("OK: Succeeded");
        CommonResponse response = new CommonResponse<>(metaData, dataList);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "센서 경고정보 조회 API", description = "센서 경고정보를 조회합니다.(프론트엔드 <-> 백엔드)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "sensor data post : SUCCESS"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @GetMapping("/users/{userId}/sensor-warning")
    public ResponseEntity<CommonResponse<SensorWarningResponse>> getSensorsWarning(@PathVariable Long userId) {
        SensorWarningResponse dataList = sensorService.getSensorsWarning(userId);

        MetaData metaData = MetaData.of("OK: Succeeded");
        CommonResponse response = new CommonResponse<>(metaData, dataList);
        return ResponseEntity.ok(response);
    }
}
