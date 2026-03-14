package com.jimmy.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// 角色菜单关联表
@Data
@Entity(name = "role_menu")
public class RoleMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Column(name = "create_at")
    private LocalDateTime createAt;
}
