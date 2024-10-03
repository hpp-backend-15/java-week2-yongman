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
import io.hhplus.lecture.presentation.dto.HistoryResponseDto;
import io.hhplus.lecture.presentation.dto.LectureDetailResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
            lectureService.apply(userId, lectureId);
        });
    }

    @Test
    @Transactional
    @DisplayName("통합테스트 - 이미 강의 신청")
    void applyLecture_AlreadyApply() {
        long userId = 1L;
        User user = new User(userId, "이용만");
        userRepository.saveUser(user);

        Lecture lecture = new Lecture("항해플러스", 30, 5);
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
    void applyLectureTest() {
        long userId = 1L;
        User user = new User(userId, "이용만");
        userRepository.saveUser(user);

        Lecture lecture = new Lecture("항해플러스", 30, 5);
        lectureRepository.saveLecture(lecture);

        LectureHistory lectureHistory = lectureService.apply(user.getId(), lecture.getId());

        assertEquals(6, lecture.getCurrentStudent());
        assertEquals(userId, lectureHistory.getUser().getId());
        assertEquals(lecture.getId(), lectureHistory.getLecture().getId());
        assertTrue(lectureHistory.getIsApplied());
    }

    @Test
    @Transactional
    @DisplayName("통합테스트 - 강의 신청 여부 - 회원 정보 없음")
    void checkApplyLecture_UserNotFound() {
        //given
        Long userId = 1L;
        Lecture lecture = new Lecture("항해플러스", 30, 0);
        Lecture saveLecture = lectureRepository.saveLecture(lecture);

        //when&then
        assertThrows(UserNotFoundException.class, () -> {
            lectureService.checkApplyLecture(userId, saveLecture.getId());
        });
    }

    @Test
    @Transactional
    @DisplayName("통합테스트 - 강의 신청 여부 - 강의 정보 없음")
    void checkApplyLecture_LectureNotFound() {
        //given
        User user = new User("이용만");
        User saveUser = userRepository.saveUser(user);

        Long lectureId = 1L;

        //when&then
        assertThrows(LectureNotFoundException.class, () -> {
            lectureService.checkApplyLecture(saveUser.getId(), lectureId);
        });
    }

    @Test
    @Transactional
    @DisplayName("통합테스트 - 강의 신청 여부 - 강의 신청 X")
    void checkApplyLecture_NotAppliedLecture() {
        //given
        User user = new User("이용만");
        User saveUser = userRepository.saveUser(user);

        Lecture lecture = new Lecture("항해플러스", 30, 1);
        Lecture saveLecture = lectureRepository.saveLecture(lecture);

        //when
        boolean isApplied = lectureService.checkApplyLecture(saveUser.getId(), saveLecture.getId());

        //then
        assertFalse(isApplied);
    }

    @Test
    @Transactional
    @DisplayName("통합테스트 - 강의 신청 여부 - 강의 신청 O")
    void checkApplyLecture_AppliedLecture() {
        //given
        User user = new User("이용만");
        User saveUser = userRepository.saveUser(user);

        Lecture lecture = new Lecture("항해플러스", 30, 1);
        Lecture saveLecture = lectureRepository.saveLecture(lecture);

        LectureHistory lectureHistory = LectureHistory.applyLectureHistory(saveUser, saveLecture);
        lectureHistoryRepository.saveLectureHistory(lectureHistory);

        //when
        boolean isApplied = lectureService.checkApplyLecture(saveUser.getId(), saveLecture.getId());

        //then
        assertTrue(isApplied);
    }

    @Test
    @Transactional
    @DisplayName("통합테스트 - 특정 유저 특강 신청 목록 조회 - 유저 X ")
    void getLectureList_UserNotFound() {
        Long userId = 1L;  // DB에 없는 유저 ID를 가정

        assertThrows(UserNotFoundException.class, () -> {
            lectureService.getLectureList(userId);
        });
    }

    @Test
    @Transactional
    @DisplayName("통합테스트 - 특정 유저 특강 신청 목록 조회 - 신청 목록 X")
    void getLectureList_LectureNotFound(){
        User user = new User("이용만");
        User saveUser = userRepository.saveUser(user);

        assertThrows(LectureNotFoundException.class, () -> {
            lectureService.getLectureList(saveUser.getId());
        });
    }

    @Test
    @Transactional
    @DisplayName("통합테스트 - 특정 유저 특강 신청 목록 조회 - 정상 실행")
    void getLectureList_Success(){
        User user = new User("이용만");
        User saveUser = userRepository.saveUser(user);

        Lecture lecture = new Lecture("항해플러스", 30, 0 ,"허재");
        lectureRepository.saveLecture(lecture);

        LectureHistory lectureHistory = LectureHistory.applyLectureHistory(saveUser,lecture);
        lectureHistoryRepository.saveLectureHistory(lectureHistory);


        List<HistoryResponseDto> responseDtos = lectureService.getLectureList(saveUser.getId());

        assertEquals(1, responseDtos.size());
    }

    @Test
    @Transactional
    @DisplayName("통합테스트 - 특강 선택")
    void selectLecture_Success(){
        LocalDateTime currentTime = LocalDateTime.of(2024,10,4,12,39);

        Lecture lecture1 = new Lecture("자바",30, 5, "허재", LocalDateTime.of(2024,10,10,10,0));
        Lecture lecture2 = new Lecture("코틀린",30, 4, "렌", LocalDateTime.of(2024,10,11,10,0));
        Lecture lecture3 = new Lecture("TS",30, 3, "하헌우", LocalDateTime.of(2024,10,15,10,0));

        lectureRepository.saveLecture(lecture1);
        lectureRepository.saveLecture(lecture2);
        lectureRepository.saveLecture(lecture3);

        List<LectureDetailResponseDto> result = lectureService.selectLecture(currentTime);

        assertEquals(2,result.size());
        assertTrue(result.stream().anyMatch(lecture -> lecture.getTitle().equals("자바")));
        assertTrue(result.stream().anyMatch(lecture -> lecture.getTitle().equals("코틀린")));
        assertFalse(result.stream().anyMatch(lecture -> lecture.getTitle().equals("타입스크립트")));
    }
}
