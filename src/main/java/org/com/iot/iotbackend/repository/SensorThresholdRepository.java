package org.com.iot.iotbackend.repository;

import org.com.iot.iotbackend.entity.sensor.Sensor;
import org.com.iot.iotbackend.entity.sensor.SensorThreshold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SensorThresholdRepository extends JpaRepository<SensorThreshold, Long> {
    @Modifying
    @Query(value = "DELETE FROM sensor_threshold st WHERE (st.sensor_id, st.sensor_type) IN (SELECT s.sensor_id, s.sensor_type  FROM sensor s WHERE s.user_id = :userId)", nativeQuery = true)
    void deleteBySensorUserIdNative(@Param("userId") Long userId);

    SensorThreshold findBySensor(Sensor sensor);
}
