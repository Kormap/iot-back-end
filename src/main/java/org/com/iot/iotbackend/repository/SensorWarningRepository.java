package org.com.iot.iotbackend.repository;

import org.com.iot.iotbackend.dto.sensor.TodayWarningDTO;
import org.com.iot.iotbackend.entity.sensor.SensorWarning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SensorWarningRepository extends JpaRepository<SensorWarning, Long> {
    @Modifying
    @Query(value = "DELETE FROM sensor_warning sw WHERE sw.threshold_id IN (SELECT st.id FROM sensor_threshold st JOIN sensor s ON st.sensor_id = s.sensor_id and st.sensor_type = s.sensor_type WHERE s.user_id = :userId)", nativeQuery = true)
    void deleteBySensorUserIdNative(@Param("userId") Long userId);

    @Query(value = "SELECT sw.*, st.sensor_type FROM sensor_warning sw JOIN sensor_threshold st ON sw.threshold_id = st.id JOIN sensor s ON st.sensor_id = s.sensor_id AND st.sensor_type = s.sensor_type WHERE s.user_id = :userId AND DATE(sw.warning_at) = CURRENT_DATE", nativeQuery = true)
    List<TodayWarningDTO> findTodayWarningsByUserId(@Param("userId") Long userId);
}
