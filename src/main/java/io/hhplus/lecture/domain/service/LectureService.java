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
import io.hhplus.lecture.presentation.dto.HistoryResponseDto;
import io.hhplus.lecture.presentation.dto.LectureDetailResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class LectureService {
    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final LectureHistoryRepository lectureHistoryRepository;

    private ReentrantLock lock = new ReentrantLock();


    public LectureService(UserRepository userRepository, LectureRepository lectureRepository, LectureHistoryRepository lectureHistoryRepository) {
        this.userRepository = userRepository;
        this.lectureRepository = lectureRepository;
        this.lectureHistoryRepository = lectureHistoryRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED) // 격리 수준이 없을 때 없을 때 Dirty Read 발생
    public LectureHistory apply(Long userId, Long lectureId) {
        try {
            lock.lock();
        // 유저 존재 여부
        User findUser = userRepository.findByUser(userId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

        // 강의 존재 여부
        Lecture findLecture = lectureRepository.findLectureWithPessimisticLock(lectureId).orElseThrow(() -> new LectureNotFoundException("강의가 존재하지 않습니다."));

        // 수강생 초과 여부
        if (findLecture.getMaxStudent() == findLecture.getCurrentStudent()) throw new MaxStudentException("수강생 초과이므로 신청할 수 없습니다.");

        // 수강 신청 여부
        if (lectureHistoryRepository.isAppliedLecture(findUser, findLecture)) throw new AlreadyAppliedException("이미 신청한 강의입니다.");

        LectureHistory lectureHistory = LectureHistory.applyLectureHistory(findUser,findLecture);
        findLecture.plusStudent();

        return lectureHistoryRepository.saveLectureHistory(lectureHistory);

        }finally {
            lock.unlock();
        }
    }

    @Transactional
    public boolean checkApplyLecture(Long userId, Long lectureId){
        User findUser = userRepository.findByUser(userId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

        Lecture findLecture = lectureRepository.findLecture(lectureId).orElseThrow(() -> new LectureNotFoundException("강의가 존재하지 않습니다."));

        return lectureHistoryRepository.isAppliedLecture(findUser,findLecture);
    }

    @Transactional
    public List<HistoryResponseDto> getLectureList(Long userId){
        User findUser = userRepository.findByUser(userId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

        List<LectureHistory> lectureHistories = lectureHistoryRepository.findUserLectures(findUser);

        if (lectureHistories.isEmpty()) throw new LectureNotFoundException("신청한 강의가 없습니다.");

        return lectureHistories.stream()
                .map(lectureHistory -> new HistoryResponseDto(
                        lectureHistory.getLecture().getId(),
                        lectureHistory.getUser().getName(),
                        lectureHistory.getLecture().getLecturer()
                ))
                .collect(Collectors.toList());
    }

    /*
     * 지금 10월 3일 11시 45분에 특강 목록을 조회하려고할 때
     * 특강 목록이 아래와 같이 있다면
     * 1) openDate 10월 9일 자바
     * 2) openDate 10월 10일 코틀린
     * 3) openDate 10월 15일 타입스크립트
     * 1번과 2번의 데이터만 읽을 수 있도록 기능을 구성해봤다.
     */
    @Transactional
    public List<LectureDetailResponseDto> selectLecture(LocalDateTime currentTime){
        LocalDateTime endDate = currentTime.plusDays(7);

        List<Lecture> lectures = lectureRepository.findLecturesWithinDateRange(currentTime,endDate);

        return lectures.stream()
                .map(lecture -> new LectureDetailResponseDto(
                        lecture.getTitle(),
                        lecture.getLecturer(),
                        lecture.getOpenDate(),
                        lecture.getCurrentStudent()
                ))
                .collect(Collectors.toList());
    }
}
