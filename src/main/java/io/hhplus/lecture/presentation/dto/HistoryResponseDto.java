package io.hhplus.lecture.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryResponseDto {

    private Long id;

    private String userName;

    private String lecturer;

    public HistoryResponseDto(Long id, String userName, String lecturer){
        this.id = id;
        this.userName = userName;
        this.lecturer = lecturer;
    }
}
