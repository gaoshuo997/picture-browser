package com.jimmy.entity;

import com.jimmy.constant.DeleteFlag;
import com.jimmy.constant.StatusFlag;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;

    @Column(name = "update_time")
    private LocalDateTime updatedAt;

    @Column(name = "delete_flag")
    private Integer deleteFlag = DeleteFlag.NORMAL.getFlag();

    @Column(name = "token_version")
    private Integer tokenVersion = 0;

    @Column(name = "status")
    private Integer status = StatusFlag.VALID.getFlag();
}
