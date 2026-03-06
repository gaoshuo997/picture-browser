package com.jimmy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "statements")
public class Statements {

    @Id
    private String id;

    @Column(name = "order", nullable = false)
    private Integer order;

    @Column(name = "chinese", nullable = false)
    private String chinese;

    @Column(name = "english", nullable = false)
    private String english;

    @Column(name = "soundmark")
    private String soundmark;

    @Column(name = "course_id", nullable = false)
    private String courseId;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

}
