package io.hhplus.lecture.infrastructure;

import io.hhplus.lecture.domain.entity.User;
import io.hhplus.lecture.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> , UserRepository {

    Optional<User> findById(Long userId);

    @Override
    default Optional<User> findByUser(Long userId){
        return findById(userId);
    }

    @Override
    default User saveUser(User user){
        return save(user);
    }
}
