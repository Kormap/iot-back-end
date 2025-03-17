package org.com.iot.iotbackend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.com.iot.iotbackend.entity.User;

import java.util.List;

import static org.com.iot.iotbackend.entity.QUser.user;

@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<User> findUsersByEmail(String email){
        return jpaQueryFactory.selectFrom(user)
                .where(
                        user.email.like("%" + email + "%")
                )
                .orderBy(user.id.asc())
                .fetch();
    }
}
