package io.hhplus.lecture.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column
    private String lecturer;

    @Column(name = "open_date")
    private LocalDateTime openDate;

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

    public Lecture(String title, int maxStudent, int currentStudent){
        this.title = title;
        this.maxStudent = maxStudent;
        this.currentStudent = currentStudent;
    }

    public Lecture(String title, int maxStudent, int currentStudent,String lecturer){
        this.title = title;
        this.maxStudent = maxStudent;
        this.currentStudent = currentStudent;
        this.lecturer = lecturer;
    }

    public Lecture(String title, Integer maxStudent, Integer currentStudent, String lecturer, LocalDateTime openDate) {
        this.title = title;
        this.maxStudent = maxStudent;
        this.currentStudent = currentStudent;
        this.lecturer = lecturer;
        this.openDate = openDate;
    }

    public Lecture(long id, String title, int maxStudent, int currentStudent, String lecturer) {
        this.id = id;
        this.title = title;
        this.maxStudent = maxStudent;
        this.currentStudent = currentStudent;
        this.lecturer = lecturer;


    }

    public void plusStudent(){
        this.currentStudent++;
    }
}
