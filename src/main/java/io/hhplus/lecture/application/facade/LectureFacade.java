package io.hhplus.lecture.application.facade;

import io.hhplus.lecture.domain.entity.LectureHistory;
import io.hhplus.lecture.domain.service.LectureService;
import org.springframework.stereotype.Component;

@Component
public class LectureFacade {
    private final LectureService lectureService;

    public LectureFacade(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    public LectureHistory apply(Long userId, Long lectureId){
        return lectureService.apply(userId,lectureId);
    }
}
