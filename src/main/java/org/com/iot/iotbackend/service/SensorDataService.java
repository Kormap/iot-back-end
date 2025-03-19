package org.com.iot.iotbackend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.iot.iotbackend.dto.sensor.DHTSensor;
import org.com.iot.iotbackend.dto.sensor.SensorDataDTO;
import org.com.iot.iotbackend.dto.sensor.SoilMoistureSensor;
import org.com.iot.iotbackend.dto.user.UserDTO;
import org.com.iot.iotbackend.entity.User;
import org.com.iot.iotbackend.repository.SensorDataRepository;
import org.com.iot.iotbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorDataService {
    private final UserRepository userRepository;
    private final SensorDataRepository sensorDataRepository;

    @Transactional
    public void saveSensorData(SensorDataDTO sensorData) {
        UserDTO user = sensorData.getUser();
        User userInfo =  userRepository.findUserByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 온습도(DHT) 센서 저장
        DHTSensor dhtSensor = sensorData.getDhtSensor();
        sensorDataRepository.save(dhtSensor.toTemperatureEntity(userInfo)); // 온도센서 저장
        sensorDataRepository.save(dhtSensor.toHumidityEntity(userInfo));    // 습도센서 저장

        // 토양수분(SOIL_MOISTURE) 센서 저장
        SoilMoistureSensor soilMoistureSensor = sensorData.getSoilMoistureSensor();
        sensorDataRepository.save(soilMoistureSensor.toEntity(userInfo));

        // TODO : 저장 후 설정된 임계값에 포함되지 않을 경우 경고
    }

}
