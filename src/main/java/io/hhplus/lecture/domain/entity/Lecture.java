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

    public Lecture(Long id, String title,int currentStudent){
        this.id = id;
        this.title = title;
        this.currentStudent = currentStudent;
    }

    public Lecture(Long id, String title,int maxStudent,int currentStudent){
        this.id = id;
        this.title = title;
        this.maxStudent = maxStudent;
        this.currentStudent = currentStudent;
    }

    public void plusStudent(){
        this.currentStudent++;
    }
}
