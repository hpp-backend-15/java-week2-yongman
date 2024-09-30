package io.hhplus.lecture.domain.repository;

import io.hhplus.lecture.domain.entity.User;

public interface UserRepository {

    User findByUser(Long userId);
}
