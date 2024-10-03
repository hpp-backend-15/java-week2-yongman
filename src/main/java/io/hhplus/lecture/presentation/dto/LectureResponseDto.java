package io.hhplus.lecture.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LectureResponseDto {
    private boolean status;

    public LectureResponseDto(boolean status){
        this.status = status;
    }
}
