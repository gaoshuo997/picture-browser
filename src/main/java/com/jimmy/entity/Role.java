package com.jimmy.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", nullable = false, length = 32)
    private String roleName;

    @Column(name = "role_code", nullable = false, length = 32)
    private String roleCode;

    @Column(name = "description", length = 128)
    private String  description;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "delete_flag")
    private Integer deleteFlag = 0;

    @Column(name = "status")
    private Integer status = 1;

}
