package com.jimmy.resp;

import lombok.Data;

@Data
public class FootballerResp{
    private Long id;

    private String name;

    private String position;

    private Integer age;

    private String team;

    private String description;
    // 球员头像
    private byte[] imageData;
}
