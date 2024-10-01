package io.hhplus.lecture.domain.repository;

import io.hhplus.lecture.domain.entity.Lecture;

import java.util.Optional;

public interface LectureRepository {

    Optional<Lecture> findLecture(Long lectureId);

    Lecture saveLecture(Lecture lecture);
}
