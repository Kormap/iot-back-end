package org.com.iot.iotbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.com.iot.iotbackend.entity.sensor.Sensor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "name", nullable = false, length = 10)
    private String name;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "phone_number", nullable = false, length = 11)
    private String phoneNumber;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    private List<Sensor> sensors;

    // 기본 생성자
    protected User() {
    }

    public User(String email, String name, String password, String phoneNumber) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public void setEncodedPassword(String encodedInputPassword) {
        this.password = encodedInputPassword;
    }

    public String getPassword() {
        return this.password;
    }
}