package io.hhplus.lecture.infrastructure;

import io.hhplus.lecture.domain.entity.User;
import io.hhplus.lecture.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> , UserRepository {

    @Override
    default User findByUser(Long userId){
        return findById(userId).orElse(null);
    }
}
