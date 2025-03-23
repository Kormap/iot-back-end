package org.com.iot.iotbackend.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.iot.iotbackend.dto.sensor.*;
import org.com.iot.iotbackend.dto.user.request.UserDTO;
import org.com.iot.iotbackend.entity.User;
import org.com.iot.iotbackend.entity.sensor.Sensor;
import org.com.iot.iotbackend.entity.sensor.SensorData;
import org.com.iot.iotbackend.entity.sensor.SensorThreshold;
import org.com.iot.iotbackend.entity.sensor.SensorWarning;
import org.com.iot.iotbackend.repository.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorService {
    private final UserRepository userRepository;
    private final SensorDataRepository sensorDataRepository;
    private final SensorThresholdRepository sensorThresholdRepository;
    private final SensorRepository sensorRepository;

    private final ModelMapper modelMapper;
    private final SensorWarningRepository sensorWarningRepository;

    @PostConstruct
    public void init() {
        // ModelMapper 설정, 센서 객체 중 센서 타입만 변환
        modelMapper.addMappings(new PropertyMap<SensorThreshold, SensorThresholdResponse>() {
            @Override
            protected void configure() {
                map(source.getSensor().getSensorType(), destination.getSensorType());  // sensor 객체의 sensorType만 매핑
            }
        });
    }

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
        Type listType = new TypeToken<List<SensorDataResponse>>() {
        }.getType();

        // ModelMapper를 사용하여 SensorData 리스트를 SensorDataResponse 리스트로 변환
        List<SensorDataResponse> sensorDataResponse = modelMapper.map(sensorDataList, listType);

        return sensorDataResponse;
    }

    public List<SensorThresholdResponse> getSensorsThreshold(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 센서 정보 조회
        List<Sensor> sensorList = sensorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Sensor info not found"));

        // 센서 임계값(설정범위) 조회
        List<SensorThreshold> thresholds = new ArrayList<>();
        sensorList.forEach(sensor -> {
            SensorThreshold sensorThreshold = sensorThresholdRepository.findBySensor(sensor);

            if (sensorThreshold != null) {
                thresholds.add(sensorThresholdRepository.findBySensor(sensor));
            }
        });

        // 엔티티 리스트 -> 응답 리스트 객체로 변환
        Type listType = new TypeToken<List<SensorThresholdResponse>>() {
        }.getType();
        // ModelMapper를 사용하여 SensorThreshold 리스트를 SensorThresholdResponse 리스트로 변환
        List<SensorThresholdResponse> sensorThresholdResponse = modelMapper.map(thresholds, listType);

        return sensorThresholdResponse;
    }

    public SensorWarningResponse getSensorsWarning(Long userId) {
        List<TodayWarningDTO> todayWarnings = sensorWarningRepository.findTodayWarningsByUserId(userId);

        SensorWarningResponse response = new SensorWarningResponse();

        // 임계값(설정값) 이하,이상 카운트
        todayWarnings.forEach(sw -> {
            // 경고날짜가 오늘날짜인지 체크
            if (LocalDate.now().equals(sw.getWarningAt().toLocalDate())) {
                // TODO(추후적용) 센서타입 관리방안(하드코딩이 아닌 ENUM 혹은 데이터베이스 구조)
                if("DHT_TEMPERATURE".equals(sw.getSensorType()) || "DHT_HUMIDITY".equals(sw.getSensorType())) {
                    if ("MIN".equals(sw.getWarningType())) {
                        response.setDhtMinWarningCount(response.getDhtMinWarningCount() + 1);
                    } else if ("MAX".equals(sw.getWarningType())) {
                        response.setDhtMaxWarningCount(response.getDhtMaxWarningCount() + 1);
                    }
                } else if("SOIL_MOISTURE".equals(sw.getSensorType())) {
                    if ("MIN".equals(sw.getWarningType())) {
                        response.setSoilMinWarningCount(response.getSoilMinWarningCount() + 1);
                    } else if ("MAX".equals(sw.getWarningType())) {
                        response.setSoilMaxWarningCount(response.getSoilMaxWarningCount() + 1);
                    }
                }
            }
        });
        return response;
    }
}
