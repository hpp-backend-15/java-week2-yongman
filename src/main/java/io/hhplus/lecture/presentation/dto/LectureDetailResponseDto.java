package io.hhplus.lecture.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LectureDetailResponseDto {

    private String title;
    private String lecturer;
    private LocalDateTime openDate;
    private Integer currentStudent;

    public LectureDetailResponseDto(String title, String lecturer, LocalDateTime openDate, Integer currentStudent) {
        this.title = title;
        this.lecturer = lecturer;
        this.openDate = openDate;
        this.currentStudent = currentStudent;
    }
}
