package org.com.iot.iotbackend.entity.sensor;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "sensor_threshold",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"sensor_id", "sensor_type"}) // 복합 유니크 키 설정
        }
)
public class SensorThreshold {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 ID

    @Column(name = "min_threshold", nullable = false, precision = 10, scale = 2)
    private BigDecimal minThreshold; // 최소 임계값

    @Column(name = "max_threshold", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxThreshold; // 최대 임계값

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 생성일시

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일시

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "threshold", cascade = CascadeType.ALL)
    private List<SensorWarning> warnings; // 센서 경고 목록

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "sensor_id", referencedColumnName = "sensor_id"),
            @JoinColumn(name = "sensor_type", referencedColumnName = "sensor_type")
    }) // FK: sensor 테이블 참조 (복합 키)
    private Sensor sensor; // 센서 정보
}
