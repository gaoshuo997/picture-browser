package com.jimmy.resp;

import lombok.Data;

import java.util.List;

@Data
public class CoursePacksResp {
    private Long id;

    private String title;

    private String description;

    private Boolean free;

    private String cover;

    private String coverFileName;

    private List<CourseResp> courses;

    private Integer courseCount;

    private String createdAt;
}
