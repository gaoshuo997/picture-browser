package com.jimmy.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// 用户角色关联表
@Data
@Entity(name = "user_role")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "create_at")
    private LocalDateTime createAt;
}
