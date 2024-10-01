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
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

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

    @Transactional
    public LectureHistory apply(Long userId, Long lectureId) {
        System.out.println("유저아이디 : " + userId + " ////// 강의 아이디  : + " + lectureId);

        // 유저 존재 여부
        User findUser = userRepository.findByUser(userId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

        // 강의 존재 여부
        Lecture findLecture = lectureRepository.findLecture(lectureId).orElseThrow(() -> new LectureNotFoundException("강의가 존재하지 않습니다."));

        // 수강생 초과 여부
        if (findLecture.getMaxStudent() == findLecture.getCurrentStudent()) throw new MaxStudentException("수강생 초과이므로 신청할 수 없습니다.");

        // 수강 신청 여부
        if (lectureHistoryRepository.isAppliedLecture(findUser, findLecture)) throw new AlreadyAppliedException("이미 신청한 강의입니다.");

        LectureHistory lectureHistory = LectureHistory.applyLectureHistory(findUser,findLecture);
        System.out.println("lectureHistory 객체가 잘 생성되는가?@#%@#$!@$%#$%^$!@#" + lectureHistory);
        findLecture.plusStudent();
        System.out.println("유저아이디 : " + lectureHistory.getUser().getName() + " ////// 현재 수강 신청 인원 : " + lectureHistory.getLecture().getCurrentStudent());

        return lectureHistoryRepository.saveLectureHistory(lectureHistory);
    }
}
