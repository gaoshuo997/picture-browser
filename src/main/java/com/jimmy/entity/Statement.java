package com.jimmy.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "statement")
public class Statement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order", nullable = false, length = 32)
    private Integer order;

    @Column(name = "chinese", nullable = false)
    private String chinese;

    @Column(name = "english", nullable = false)
    private String english;

    @Column(name = "soundmark", nullable = false)
    private String soundMark;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

}
