package org.com.iot.iotbackend.entity.sensor;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_warning")
@Getter
public class SensorWarning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 ID

    @Column(name = "warning_type", nullable = false, length = 10)
    private String warningType; // 경고 유형 (min, max)

    @Column(name = "warning_at", nullable = false)
    private LocalDateTime warningAt; // 경고 발생 시간

    @Column(name = "measured_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal measuredValue; // 측정된 값

    @Column(name = "threshold_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal thresholdValue; // 초과된 임계값

    @Column(name = "unit", length = 10)
    private String unit; // 단위 (°C, %, ppm 등)

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt; // 생성일시


    @ManyToOne
    @JoinColumn(name = "threshold_id", nullable = false) // FK: sensor_threshold.id
    private SensorThreshold threshold; // 참조하는 임계값 정보
}

