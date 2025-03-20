package org.com.iot.iotbackend.repository;

import org.com.iot.iotbackend.entity.sensor.SensorWarning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SensorWarningRepository extends JpaRepository<SensorWarning, Long> {
    @Modifying
    @Query(value = "DELETE FROM sensor_warning sw WHERE sw.threshold_id IN (SELECT st.id FROM sensor_threshold st JOIN sensor s ON st.sensor_id = s.sensor_id and st.sensor_type = s.sensor_type WHERE s.user_id = :userId)", nativeQuery = true)
    void deleteBySensorUserIdNative(@Param("userId") Long userId);
}
