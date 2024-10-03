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
import io.hhplus.lecture.presentation.dto.LectureResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LectureServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private LectureHistoryRepository lectureHistoryRepository;

    @InjectMocks
    private LectureService lectureService;

    @Test
    @DisplayName("회원 정보 없음")
    void applyLecture_User_Not_Found(){
        //given
        Long userId = 1L;
        Long lectureId = 1L;

        //when
        when(userRepository.findByUser(userId)).thenReturn(Optional.empty());

        //then
        assertThrows(UserNotFoundException.class, () -> {
            lectureService.apply(userId,lectureId);
        });

        verify(userRepository,times(1)).findByUser(userId);
    }

    @Test
    @DisplayName("강의 정보 없음")
    void applyLecture_Lecture_Not_Found(){

        //given
        long userId = 1L;
        long lectureId = 1L;
        User user = new User(userId, "이용만");

        //when
        when(userRepository.findByUser(userId)).thenReturn(Optional.of(user));
        when(lectureRepository.findLecture(lectureId)).thenReturn(Optional.empty());

        //then
        assertThrows(LectureNotFoundException.class, () ->{
           lectureService.apply(userId,lectureId);
        });

        verify(lectureRepository,times(1)).findLecture(lectureId);
    }

    @Test
    @DisplayName("수강생 초과")
    void applyLecture_Max_Student(){
        //given
        long userId = 1L;
        long lectureId = 1L;
        User user = new User(userId, "이용만");
        Lecture lecture = new Lecture(lectureId, "항해플러스",30);

        //when
        when(userRepository.findByUser(userId)).thenReturn(Optional.of(user));
        when(lectureRepository.findLecture(lectureId)).thenReturn(Optional.of(lecture));

        //then
        assertThrows(MaxStudentException.class, ()-> {
            lectureService.apply(userId,lectureId);
        });

        verify(userRepository,times(1)).findByUser(userId);
        verify(lectureRepository,times(1)).findLecture(lectureId);
    }

    @Test
    @DisplayName("이미 강의 신청")
    void applyLecture_Already_apply(){
        //given
        long userId = 1L;
        long lectureId = 1L;
        User user = new User(userId,"이용만");
        Lecture lecture = new Lecture(lectureId,"항해플러스",1);

        //when
        when(userRepository.findByUser(userId)).thenReturn(Optional.of(user));
        when(lectureRepository.findLecture(lectureId)).thenReturn(Optional.of(lecture));
        when(lectureHistoryRepository.isAppliedLecture(user,lecture)).thenReturn(true);

        //then
        assertThrows(AlreadyAppliedException.class,() -> {
           lectureService.apply(userId,lectureId);
        });

        verify(userRepository,times(1)).findByUser(userId);
        verify(lectureRepository,times(1)).findLecture(lectureId);
        verify(lectureHistoryRepository,times(1)).isAppliedLecture(user,lecture);
    }

    @Test
    @DisplayName("정상적인 강의 신청")
    void applyLectureTest(){

        //given
        long userId = 1L;
        long lectureId = 1L;
        User user = new User(userId, "이용만");
        Lecture lecture = new Lecture(lectureId, "항해플러스",1);
        LectureHistory lectureHistory = new LectureHistory(user,lecture);

        //when
        when(userRepository.findByUser(userId)).thenReturn(Optional.of(user));
        when(lectureRepository.findLecture(lectureId)).thenReturn(Optional.of(lecture));
        when(lectureHistoryRepository.isAppliedLecture(user,lecture)).thenReturn(false);
        when(lectureHistoryRepository.saveLectureHistory(any(LectureHistory.class))).thenReturn(lectureHistory);

        //then
        //강의 신처잉 정상적으로 이루어졌는지 검증
        LectureHistory result = lectureService.apply(userId,lectureId);

        assertEquals(lectureHistory, result);
        assertEquals(2,lecture.getCurrentStudent());

        verify(userRepository,times(1)).findByUser(userId);
        verify(lectureRepository,times(1)).findLecture(lectureId);
        verify(lectureHistoryRepository,times(1)).isAppliedLecture(user,lecture);
        verify(lectureHistoryRepository,times(1)).saveLectureHistory(any(LectureHistory.class));
    }


    //강의 신청 여부 테스트
    @Test
    @DisplayName("강의신청여부 - 유저 정보 없음")
    void checkApplyLecture_UserNotFound(){
        //given
        Long userId = 1L;
        Long lectureId = 1L;

        //when
        when(userRepository.findByUser(userId)).thenReturn(Optional.empty());

        //then
        assertThrows(UserNotFoundException.class, () -> {
           lectureService.checkApplyLecture(userId,lectureId);
        });
    }

    @Test
    @DisplayName("강의신청여부 - 강의 정보 없음")
    void checkApplyLecture_LectureNotFound(){
        //given
        long userId = 1L;
        long lectureId = 1L;
        User user = new User(userId, "이용만");

        //when
        when(userRepository.findByUser(userId)).thenReturn(Optional.of(user));

        //then
        assertThrows(LectureNotFoundException.class, () -> {
           lectureService.checkApplyLecture(userId,lectureId);
        });
    }

    @Test
    @DisplayName("강의신청여부 - 강의를 신청하지 않음")
    void checkApplyLecture_NotAppliedLecture(){
        //given
        long userId = 1L;
        long lectureId = 1L;
        User user = new User(userId,"이용만");
        Lecture lecture = new Lecture(lectureId,"항해플러스",30,1);

        //when
        when(userRepository.findByUser(userId)).thenReturn(Optional.of(user));
        when(lectureRepository.findLecture(lectureId)).thenReturn(Optional.of(lecture));
        when(lectureHistoryRepository.isAppliedLecture(user,lecture)).thenReturn(false);

        boolean isApplied = lectureService.checkApplyLecture(userId,lectureId);

        //then
        assertFalse(isApplied);
    }

    @Test
    @DisplayName("강의신청여부 - 정상 강의 신청여부")
    void checkApplyLecture_AppliedLecture(){
        //given
        long userId = 1L;
        long lectureId = 1L;
        User user = new User(userId,"이용만");
        Lecture lecture = new Lecture(lectureId,"항해플러스", 30,1);

        //when
        when(userRepository.findByUser(userId)).thenReturn(Optional.of(user));
        when(lectureRepository.findLecture(lectureId)).thenReturn(Optional.of(lecture));
        when(lectureHistoryRepository.isAppliedLecture(user,lecture)).thenReturn(true);

        boolean isApplied = lectureService.checkApplyLecture(userId,lectureId);

        //then
        assertTrue(isApplied);
    }

    @Test
    @DisplayName("강의 목록 조회 - 유저 존재 X")
    void getLectures_UserNotFound(){
        long userId = 1L;

        when(userRepository.findByUser(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            lectureService.getLectureList(userId);
        });
    }

    @Test
    @DisplayName("강의 목록 조회 - 유저의 강의 신청 목록 X")
    void getLectures_NotFoundAppliedLectures(){
        long userId = 1L;
        User user = new User("이용만");

        when(userRepository.findByUser(userId)).thenReturn(Optional.of(user));
        when(lectureHistoryRepository.findUserLectures(user)).thenReturn(List.of());

        assertThrows(LectureNotFoundException.class, () -> {
            lectureService.getLectureList(userId);
        });
    }

    @Test
    @DisplayName("강의 목록 조회 - 유저의 강의 신청 목록")
    void getLectures_SuccessAppliedLectures(){
        long userId = 1L;
        User user = new User(userId, "이용만");

        Lecture lecture1 = new Lecture(1L,"항해플러스", 30,0,"허재");
        Lecture lecture2 = new Lecture(2L,"자바스터디", 30,0,"렌");

        LectureHistory lectureHistory = LectureHistory.applyLectureHistory(user,lecture1);
        LectureHistory lectureHistory1 = LectureHistory.applyLectureHistory(user,lecture2);

        when(userRepository.findByUser(userId)).thenReturn(Optional.of(user));
        when(lectureHistoryRepository.findUserLectures(user)).thenReturn(List.of(lectureHistory,lectureHistory1));

        List<HistoryResponseDto> responseDtos = lectureService.getLectureList(userId);

        //then
        assertEquals(2,responseDtos.size());
    }
}
