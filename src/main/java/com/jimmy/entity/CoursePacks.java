package com.jimmy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

// 课程包
@Data
@Entity
@Table(name = "course_packs")
public class CoursePacks {

    @Id
    private String id;

    @Column(name = "order", nullable = false)
    private Integer order;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "is_free")
    private Boolean free;

    @Column(name = "cover")
    private String cover;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "share_level")
    private String shareLevel;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}
