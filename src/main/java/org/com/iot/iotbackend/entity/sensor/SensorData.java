package org.com.iot.iotbackend.entity.sensor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.com.iot.iotbackend.entity.User;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_data")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 ID

    @Column(name = "sensor_id", nullable = false, length = 50)
    private String sensorId; // 센서 ID

    @Column(name = "sensor_type", nullable = false, length = 50)
    private String sensorType; // 센서 타입

    @Column(name = "measured_value", precision = 10, scale = 2)
    private BigDecimal measuredValue; // 측정값

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt; // 측정일시

    @Column(name = "unit", length = 10)
    private String unit; // 단위 (예: °C, %, ppm 등)

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt; // 생성일시

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
