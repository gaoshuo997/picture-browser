package com.jimmy.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// 课程包
@Data
@Entity
@Table(name = "course_packs")
public class CoursePacks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 32)
    private String title;

    @Column(name = "description", length = 64)
    private String description;

    @Column(name = "is_free")
    private Boolean free;

    @Column(name = "cover", nullable = false)
    private Long cover;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
