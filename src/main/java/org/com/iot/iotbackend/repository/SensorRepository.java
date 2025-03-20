package org.com.iot.iotbackend.repository;

import org.com.iot.iotbackend.entity.sensor.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
    @Modifying
    @Query("DELETE FROM Sensor s WHERE s.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
