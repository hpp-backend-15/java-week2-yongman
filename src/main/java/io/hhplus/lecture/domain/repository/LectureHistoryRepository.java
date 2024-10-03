package io.hhplus.lecture.domain.repository;

import io.hhplus.lecture.domain.entity.Lecture;
import io.hhplus.lecture.domain.entity.LectureHistory;
import io.hhplus.lecture.domain.entity.User;

import java.util.List;

public interface LectureHistoryRepository {

    Boolean isAppliedLecture(User user, Lecture lecture);

    LectureHistory findLectureHistory(User user, Lecture lecture);

    LectureHistory saveLectureHistory(LectureHistory lectureHistory);

    List<LectureHistory> findUserLectures(User user);
}
