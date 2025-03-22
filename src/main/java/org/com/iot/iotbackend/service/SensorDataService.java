package org.com.iot.iotbackend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.iot.iotbackend.dto.sensor.DHTSensor;
import org.com.iot.iotbackend.dto.sensor.SensorDataDTO;
import org.com.iot.iotbackend.dto.sensor.SensorDataResponse;
import org.com.iot.iotbackend.dto.sensor.SoilMoistureSensor;
import org.com.iot.iotbackend.dto.user.request.UserDTO;
import org.com.iot.iotbackend.entity.User;
import org.com.iot.iotbackend.entity.sensor.SensorData;
import org.com.iot.iotbackend.repository.SensorDataRepository;
import org.com.iot.iotbackend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorDataService {
    private final UserRepository userRepository;
    private final SensorDataRepository sensorDataRepository;

    private final ModelMapper modelMapper;

    @Transactional
    public void saveSensorData(SensorDataDTO sensorData) {
        UserDTO user = sensorData.getUser();
        User userInfo = userRepository.findUserByEmail(user.getEmail())
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

    public List<SensorDataResponse> getSensorsData(Long userId, String sensorType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();  // 오늘 날짜
        LocalDateTime startOfDay = today.atStartOfDay();  // 오늘 자정
        LocalDateTime endOfDay = today.atTime(23, 59, 59, 999999999);  // 오늘 마지막 시간 (23:59:59.999999999)

        List<SensorData> sensorDataList = sensorDataRepository.findSensorDataForToday(userId, sensorType, startOfDay, endOfDay);

        // 엔티티 리스트 -> 응답 리스트 객체로 변환
        Type listType = new TypeToken<List<SensorDataResponse>>() {}.getType();

        // ModelMapper를 사용하여 SensorData 리스트를 SensorDataResponse 리스트로 변환
        List<SensorDataResponse> sensorDataResponseList = modelMapper.map(sensorDataList, listType);

        return sensorDataResponseList;
    }

}
