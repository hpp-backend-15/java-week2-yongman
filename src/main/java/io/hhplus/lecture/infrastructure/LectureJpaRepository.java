package io.hhplus.lecture.infrastructure;

import io.hhplus.lecture.domain.entity.Lecture;
import io.hhplus.lecture.domain.repository.LectureRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LectureJpaRepository extends JpaRepository<Lecture, Long> , LectureRepository {

    Optional<Lecture> findById(Long lectureId);

    @Override
    default Optional<Lecture> findLecture(Long lectureId){
        return findById(lectureId);
    }
}
