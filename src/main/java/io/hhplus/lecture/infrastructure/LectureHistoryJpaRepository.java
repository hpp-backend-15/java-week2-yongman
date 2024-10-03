package io.hhplus.lecture.infrastructure;

import io.hhplus.lecture.domain.entity.Lecture;
import io.hhplus.lecture.domain.entity.LectureHistory;
import io.hhplus.lecture.domain.entity.User;
import io.hhplus.lecture.domain.repository.LectureHistoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LectureHistoryJpaRepository extends JpaRepository<LectureHistory, Long>, LectureHistoryRepository {

    LectureHistory findByUserAndLecture(User user, Lecture lecture);

    boolean existsByUserAndLectureAndIsAppliedTrue(User user, Lecture lecture);

    List<LectureHistory> findByUserAndIsAppliedTrue(User user);


    @Override
    default Boolean isAppliedLecture(User user, Lecture lecture){
        return existsByUserAndLectureAndIsAppliedTrue(user,lecture);
    }

    @Override
    default LectureHistory findLectureHistory(User user, Lecture lecture){
        return findByUserAndLecture(user,lecture);
    }

    @Override
    default LectureHistory saveLectureHistory(LectureHistory lectureHistory){
        return save(lectureHistory);
    }

    @Override
    default List<LectureHistory> findUserLectures(User user){
        return findByUserAndIsAppliedTrue(user);
    }
}
