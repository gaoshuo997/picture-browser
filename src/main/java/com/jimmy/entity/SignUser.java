package com.jimmy.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "sign_user")
public class SignUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_name", nullable = false, length = 16)
    private String loginName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, length = 32)
    private String email;

    @Column(name = "phone", length = 16)
    private String phone;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;
}
