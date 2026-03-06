package com.jimmy.resp;

import lombok.Data;

import java.util.List;

@Data
public class CoursePacksResp {
    private String id;

    private String title;

    private String description;

    private Boolean free;

    private String cover;

    private List<CourseResp> courses;

    private Integer courseCount;

    private String createdAt;
}
