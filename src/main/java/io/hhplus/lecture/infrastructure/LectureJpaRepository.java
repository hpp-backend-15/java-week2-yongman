package io.hhplus.lecture.infrastructure;

import io.hhplus.lecture.domain.entity.Lecture;
import io.hhplus.lecture.domain.repository.LectureRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureJpaRepository extends JpaRepository<Lecture, Long> , LectureRepository {

    @Override
    default Lecture findLecture(Long lectureId){
        return findById(lectureId).orElse(null);
    }
}
