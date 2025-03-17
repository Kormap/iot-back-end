package org.com.iot.iotbackend.repository;

import org.com.iot.iotbackend.entity.User;

import java.util.List;

/* QueryDSL 적용 */
public interface UserCustomRepository {
    List<User> findUsersByEmail(String email);
}
