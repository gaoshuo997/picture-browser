package com.jimmy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

import static lombok.AccessLevel.PROTECTED;

@Data
@Entity
@Table(name = "user_course_progress")
public class UserCourseProgress {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "course_id", nullable = false)
    private String courseId;

    @Column(name = "course_pack_id", nullable = false)
    private String coursePackId;

    @Column(name = "statement_index", nullable = false)
    private Integer statementIndex;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;
}
