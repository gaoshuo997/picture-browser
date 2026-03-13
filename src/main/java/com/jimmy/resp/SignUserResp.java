package com.jimmy.resp;

import lombok.Data;

@Data
public class SignUserResp {
    private Long id;

    private String loginName;

    private String email;

    private String phone;

    private String createdAt;

    private String updatedAt;

    private Integer status;
}