package io.hhplus.lecture.domain.repository;

import io.hhplus.lecture.domain.entity.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUser(Long userId);

    User saveUser(User user);
}
