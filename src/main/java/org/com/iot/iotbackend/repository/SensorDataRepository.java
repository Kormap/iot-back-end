package org.com.iot.iotbackend.repository;

import org.com.iot.iotbackend.entity.User;
import org.com.iot.iotbackend.entity.sensor.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    @Modifying
    @Query("DELETE FROM SensorData sd WHERE sd.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Query("SELECT sd FROM SensorData sd WHERE sd.user.id = :userId AND sd.sensorType = :sensorType " +
            "AND sd.measuredAt >= :startOfDay AND sd.measuredAt <= :endOfDay ORDER BY sd.id ASC")
    List<SensorData> findSensorDataForToday(@Param("userId") Long userId,
                                            @Param("sensorType") String sensorType,
                                            @Param("startOfDay") LocalDateTime startOfDay,
                                            @Param("endOfDay") LocalDateTime endOfDay);

}