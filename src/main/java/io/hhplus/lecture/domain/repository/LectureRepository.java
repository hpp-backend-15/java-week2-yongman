package io.hhplus.lecture.domain.repository;

import io.hhplus.lecture.domain.entity.Lecture;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LectureRepository {

    Optional<Lecture> findLecture(Long lectureId);

    Lecture saveLecture(Lecture lecture);

    Optional<Lecture> findLectureWithPessimisticLock(Long lectureId);

    List<Lecture> findLecturesWithinDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
