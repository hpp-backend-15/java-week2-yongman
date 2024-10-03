package io.hhplus.lecture.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "USERS")
@NoArgsConstructor
@Getter
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column
    private String name;

    public User(Long id, String name){
        this.id = id;
        this.name = name;
    }

    public User(String name){
        this.name = name;
    }
}
