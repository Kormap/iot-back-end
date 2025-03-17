package org.com.iot.iotbackend.repository;

import org.com.iot.iotbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>,  UserCustomRepository{
    Optional<User> findUserByEmail(String email);
}
