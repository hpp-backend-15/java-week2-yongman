package io.hhplus.lecture.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "LECTURE")
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id")
    private Long id;

    @Column
    private String title;

    @Column
    private Integer maxStudent;
}
