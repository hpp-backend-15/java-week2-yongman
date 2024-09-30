package io.hhplus.lecture.domain.service;


import io.hhplus.lecture.common.exception.AlreadyAppliedException;
import io.hhplus.lecture.common.exception.LectureNotFoundException;
import io.hhplus.lecture.common.exception.MaxStudentException;
import io.hhplus.lecture.common.exception.UserNotFoundException;
import io.hhplus.lecture.domain.entity.Lecture;
import io.hhplus.lecture.domain.entity.LectureHistory;
import io.hhplus.lecture.domain.entity.User;
import io.hhplus.lecture.domain.repository.LectureHistoryRepository;
import io.hhplus.lecture.domain.repository.LectureRepository;
import io.hhplus.lecture.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LectureService {

    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final LectureHistoryRepository lectureHistoryRepository;

    public LectureService(UserRepository userRepository, LectureRepository lectureRepository, LectureHistoryRepository lectureHistoryRepository) {
        this.userRepository = userRepository;
        this.lectureRepository = lectureRepository;
        this.lectureHistoryRepository = lectureHistoryRepository;
    }

    public LectureHistory apply(Long userId, Long lectureId) {
        // 유저 존재 여부
        User findUser = userRepository.findByUser(userId);
        if (findUser == null) throw new UserNotFoundException("유저가 존재하지 않습니다.");

        // 강의 존재 여부
        Lecture findLecture = lectureRepository.findLecture(lectureId);
        if (findLecture == null) throw new LectureNotFoundException("강의가 존재하지 않습니다.");

        // 수강생 초과 여부
        if (findLecture.getMaxStudent() == findLecture.getCurrentStudent()) throw new MaxStudentException("수강생 초과이므로 신청할 수 없습니다.");

        // 수강 신청 여부
        if (lectureHistoryRepository.isAppliedLecture(findUser, findLecture)) throw new AlreadyAppliedException("이미 신청한 강의입니다.");

        LectureHistory lectureHistory = LectureHistory.applyLectureHistory(findUser,findLecture);
        findLecture.plusStudent();
        return lectureHistoryRepository.save(lectureHistory);
    }
}
