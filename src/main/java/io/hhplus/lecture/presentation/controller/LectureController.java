package io.hhplus.lecture.presentation.controller;

import io.hhplus.lecture.domain.entity.Lecture;
import io.hhplus.lecture.domain.entity.LectureHistory;
import io.hhplus.lecture.domain.service.LectureService;
import io.hhplus.lecture.presentation.dto.HistoryResponseDto;
import io.hhplus.lecture.presentation.dto.LectureApplyDto;
import io.hhplus.lecture.presentation.dto.LectureDetailResponseDto;
import io.hhplus.lecture.presentation.dto.LectureResponseDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/lecture")
public class LectureController {

    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @PostMapping("/apply")
    public ResponseEntity<String> apply(@RequestBody LectureApplyDto dto){
        Boolean isApplied = lectureService.apply(dto.getUserId(), dto.getLectureId()).getIsApplied();
        LectureResponseDto lectureResponseDto = new LectureResponseDto(isApplied);
        if (lectureResponseDto.isStatus()) return ResponseEntity.ok("강의 신청 완료했습니다.");
        else return ResponseEntity.badRequest().body("강의 신청 실패했습니다.");
    }

    //특강 신청 여부 조회 API
    @GetMapping("/enrollment/{userId}")
    public ResponseEntity<LectureResponseDto> checkApplyLecture(@PathVariable Long userId, @RequestParam(name = "lecture") Long lectureId ){
        boolean status = lectureService.checkApplyLecture(userId,lectureId);
        return ResponseEntity.ok(new LectureResponseDto(status));
    }

    //특정 userID 강의 목록 조회
    @GetMapping("/getLectures/{userId}")
    public ResponseEntity<List<HistoryResponseDto>> getLectureList(@PathVariable Long userId){
        List<HistoryResponseDto> responseDto = lectureService.getLectureList(userId);
        return ResponseEntity.ok(responseDto);
    }

    //특강 선택 API <- 특강이다보니까 이벤트성이라 생각이 들어서 강의 일주일전부터면 수강신청이 가능하게 기능을 만들었다.
    @GetMapping("/select/{lectureId}")
    public ResponseEntity<List<LectureDetailResponseDto>> selectLecture(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime currentDate){
        return ResponseEntity.ok(lectureService.selectLecture(currentDate));
    }
}
