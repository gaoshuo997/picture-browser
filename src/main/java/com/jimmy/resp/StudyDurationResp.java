package com.jimmy.resp;

import lombok.Data;

@Data
public class StudyDurationResp {

    private String id;

    private Long userId;

    private String date;

    private Integer duration;

    private String  courseId;
}
