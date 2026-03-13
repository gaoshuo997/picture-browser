package com.jimmy.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "statements")
public class Statements {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order", nullable = false)
    private Integer order;

    @Column(name = "chinese", nullable = false)
    private String chinese;

    @Column(name = "english", nullable = false)
    private String english;

    @Column(name = "soundmark")
    private String soundmark;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
