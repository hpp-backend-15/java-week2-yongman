package io.hhplus.lecture.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "LECTURE_HISTORY")
@NoArgsConstructor
@Getter
public class LectureHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lectureHistoryd;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @Column
    private LocalDateTime applyDate;

    @Column
    private Boolean isApplied;

    @Builder
    public LectureHistory(User user, Lecture lecture,LocalDateTime applyDate, Boolean isApplied){
        this.user = user;
        this.lecture = lecture;
        this.applyDate = applyDate;
        this.isApplied = isApplied;
    }

    public static LectureHistory applyLectureHistory(User user, Lecture lecture){
        return LectureHistory.builder()
                .user(user)
                .lecture(lecture)
                .applyDate(LocalDateTime.now())
                .isApplied(true)
                .build();
    }
}
