package org.com.iot.iotbackend.repository;

import org.com.iot.iotbackend.entity.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
}
