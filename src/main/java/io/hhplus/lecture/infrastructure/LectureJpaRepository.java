package io.hhplus.lecture.infrastructure;

import io.hhplus.lecture.domain.entity.Lecture;
import io.hhplus.lecture.domain.repository.LectureRepository;
import jakarta.persistence.LockModeType;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LectureJpaRepository extends JpaRepository<Lecture, Long> , LectureRepository {

    Optional<Lecture> findById(Long lectureId);

    List<Lecture> findByOpenDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Override
    default List<Lecture> findLecturesWithinDateRange(LocalDateTime startDate, LocalDateTime endDate){
        return findByOpenDateBetween(startDate, endDate);
    }

    @Override
    default Optional<Lecture> findLecture(@Param("lectureId") Long lectureId){
        return findById(lectureId);
    }

    @Override
    default Lecture saveLecture(Lecture lecture){
        return save(lecture);
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Override
    default Optional<Lecture> findLectureWithPessimisticLock(Long lectureId) {
        return findById(lectureId);
    }

}
