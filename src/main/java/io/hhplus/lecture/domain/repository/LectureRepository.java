package io.hhplus.lecture.domain.repository;

import io.hhplus.lecture.domain.entity.Lecture;

public interface LectureRepository {

    Lecture findLecture(Long lectureId);
}
