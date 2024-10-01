package io.hhplus.lecture.service;

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
import io.hhplus.lecture.domain.service.LectureService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LectureServiceIntegrationTest {

    private final LectureService lectureService;
    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final LectureHistoryRepository lectureHistoryRepository;

    @Autowired
    public LectureServiceIntegrationTest(LectureService lectureService, UserRepository userRepository, LectureRepository lectureRepository, LectureHistoryRepository lectureHistoryRepository) {
        this.lectureService = lectureService;
        this.userRepository = userRepository;
        this.lectureRepository = lectureRepository;
        this.lectureHistoryRepository = lectureHistoryRepository;
    }

    @Test
    @Transactional // 테스트가 끝나면 데이터가 자동으로 롤백
    @DisplayName("통합테스트 - 유저 정보 없음")
    void applyLecture_UserNotFound() {
        long userId = 222L;
        Lecture lecture = new Lecture("항해플러스", 30, 1);
        lectureRepository.saveLecture(lecture);

        assertThrows(UserNotFoundException.class, () -> {
            lectureService.apply(userId, lecture.getId());
        });
    }

    @Test
    @Transactional
    @DisplayName("통합테스트 - 강의 정보 없음")
    void applyLecture_LectureNotFound() {
        long userId = 1L;
        long lectureId = 1231L;
        User user = new User(userId, "이용만");
        userRepository.saveUser(user);

        assertThrows(LectureNotFoundException.class, () -> {
            lectureService.apply(userId, lectureId);
        });
    }

    @Test
    @Transactional
    @DisplayName("통합테스트 - 수강생 초과")
    void applyLecture_maxStudent() {
        long userId = 1L;
        long lectureId = 1L;
        User user = new User(userId, "이용만");
        Lecture lecture = new Lecture(lectureId, "항해플러스", 30, 30);
        userRepository.saveUser(user);
        lectureRepository.saveLecture(lecture);

        assertThrows(MaxStudentException.class, () -> {
                lectureService.apply(userId,lectureId);
        });
    }

    @Test
    @Transactional
    @DisplayName("통합테스트 - 이미 강의 신청")
    void applyLecture_AlreadyApply(){
        long userId = 1L;
        User user = new User(userId, "이용만");
        userRepository.saveUser(user);

        Lecture lecture = new Lecture("항해플러스",30,5);
        lectureRepository.saveLecture(lecture);

        LectureHistory lectureHistory = LectureHistory.builder()
                .user(user)
                .lecture(lecture)
                .applyDate(LocalDateTime.now())
                .isApplied(true)
                .build();
        lectureHistoryRepository.saveLectureHistory(lectureHistory);

        assertThrows(AlreadyAppliedException.class, () -> {
            lectureService.apply(user.getId(), lecture.getId());
        });
    }

    @Test
    @Transactional
    @DisplayName("통합테스트 - 정상 신청")
    void applyLectureTest(){
        long userId = 1L;
        User user = new User(userId,"이용만");
        userRepository.saveUser(user);

        Lecture lecture = new Lecture("항해플러스",30,5);
        lectureRepository.saveLecture(lecture);

        LectureHistory lectureHistory = lectureService.apply(user.getId(),lecture.getId());

        assertEquals(6,lecture.getCurrentStudent());
        assertEquals(userId, lectureHistory.getUser().getId());
        assertEquals(lecture.getId(), lectureHistory.getLecture().getId());
        assertTrue(lectureHistory.getIsApplied());
    }
}
