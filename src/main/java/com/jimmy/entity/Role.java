package com.jimmy.entity;

import com.jimmy.entity.enums.RoleCode;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", nullable = false, length = 32)
    private String roleName;

    @Column(name = "role_code", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private RoleCode roleCode;

    @Column(name = "description", length = 128)
    private String  description;

    @Column(name = "create_at")
    private Date createAt;

    @Column(name = "update_at")
    private Date updateAt;

    @Column(name = "delete_flag")
    private Integer deleteFlag;

}
