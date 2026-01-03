package com.jimmy.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_info")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_name", nullable = false, length = 16)
    private String loginName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "email", nullable = false, length = 32)
    private String email;

    @Column(name = "phone", length = 16)
    private String phone;

    @Column(name = "checksum")
    private String checksum;
}
