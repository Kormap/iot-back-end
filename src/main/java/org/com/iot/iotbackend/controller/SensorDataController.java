package org.com.iot.iotbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.iot.iotbackend.dto.common.CommonResponse;
import org.com.iot.iotbackend.dto.common.MetaData;
import org.com.iot.iotbackend.dto.sensor.DHTSensor;
import org.com.iot.iotbackend.dto.sensor.SensorDataDTO;
import org.com.iot.iotbackend.dto.sensor.SensorDataResponse;
import org.com.iot.iotbackend.dto.sensor.SoilMoistureSensor;
import org.com.iot.iotbackend.dto.user.request.UserDTO;
import org.com.iot.iotbackend.service.SensorDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/sensor")
public class SensorDataController {
    private final SensorDataService sensorDataService;

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

        sensorDataService.saveSensorData(sensorData);

        return ResponseEntity.ok("SUCCESS");
    }

    /*
     *   프론트엔드 <-> 백엔드 서버 통신
     */
    @Operation(summary = "온도센서 데이터 조회 API", description = "온도센서 데이터를 조회합니다.(프론트엔드 <-> 백엔드)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "sensor data post : SUCCESS"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @GetMapping("/users/{userId}/sensor-data")
    public ResponseEntity<CommonResponse<List<SensorDataResponse>>> getSensorsData(@PathVariable Long userId, @RequestParam String sensorType) {
        List<SensorDataResponse> dataList = sensorDataService.getSensorsData(userId, sensorType);

        MetaData metaData = MetaData.of("OK: Succeeded");
        CommonResponse response = new CommonResponse<>(metaData, dataList);
        return ResponseEntity.ok(response);
    }
}
