package com.jimmy.resp;

import lombok.Data;

@Data
public class StudyDurationResp {

    private Long id;

    private Long userId;

    private String date;

    private Integer duration;

    private Long  courseId;
}
