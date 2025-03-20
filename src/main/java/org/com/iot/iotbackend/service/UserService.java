package org.com.iot.iotbackend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.com.iot.iotbackend.dto.user.request.UpdateProfileRequest;
import org.com.iot.iotbackend.dto.user.response.ProfileResponse;
import org.com.iot.iotbackend.entity.User;
import org.com.iot.iotbackend.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SensorDataRepository sensorDataRepository;
    private final SensorWarningRepository sensorWarningRepository;
    private final SensorThresholdRepository sensorThresholdRepository;
    private final SensorRepository sensorRepository;

    private final ModelMapper modelMapper;

    public ProfileResponse getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return modelMapper.map(user, ProfileResponse.class);
    }

    @Transactional
    public void updateProfile(UpdateProfileRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPhoneNumber().equals(request.getPhoneNumber())) {
            throw new IllegalArgumentException("기존 번호와 동일한 번호로 변경할 수 없습니다.");
        }

        user.updatePhoneNumber(request.getPhoneNumber());
    }

    @Transactional
    public void removeUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        sensorDataRepository.deleteByUserId(userId);
        sensorWarningRepository.deleteBySensorUserIdNative(userId);
        sensorThresholdRepository.deleteBySensorUserIdNative(userId);
        sensorRepository.deleteByUserId(userId);

        userRepository.deleteById(userId);
    }
}
