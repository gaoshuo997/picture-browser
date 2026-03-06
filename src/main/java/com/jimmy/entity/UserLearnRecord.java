package com.jimmy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "user_learn_record")
@Data
public class UserLearnRecord {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "count", nullable = false)
    private Integer count;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @Column(name = "day", nullable = false)
    private LocalDate day;

    @Column(name = "duration")
    private Integer duration;
}
