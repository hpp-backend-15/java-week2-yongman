package io.hhplus.lecture.presentation.controller;

import io.hhplus.lecture.domain.service.LectureService;
import io.hhplus.lecture.presentation.dto.LectureApplyDto;
import io.hhplus.lecture.presentation.dto.LectureResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
