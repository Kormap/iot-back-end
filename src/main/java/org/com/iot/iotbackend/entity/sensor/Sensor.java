package org.com.iot.iotbackend.entity.sensor;

import jakarta.persistence.*;
import org.com.iot.iotbackend.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "sensor",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"sensor_id", "sensor_type"}) // 복합 유니크 키 설정
        }
)
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 ID

    @Column(name = "sensor_id", nullable = false, length = 50)
    private String sensorId; // 센서 ID

    @Column(name = "sensor_type", nullable = false, length = 50)
    private String sensorType; // 센서 타입

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 생성일시

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // FK: users 테이블 참조
    private User user; // 유저 정보

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sensor", cascade = CascadeType.ALL)
    private List<SensorThreshold> thresholds; // 센서 임계값 목록
}
