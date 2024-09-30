package io.hhplus.lecture.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "LECTURE")
@NoArgsConstructor
@Getter
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id")
    private Long id;

    @Column
    private String title;

    @Column
    private Integer maxStudent = 30;

    @Column
    private Integer currentStudent = 0;

    public void plusStudent(){
        this.currentStudent++;
    }
}
