package com.jimmy.resp;

import lombok.Data;

@Data
public class StatementResp {

    private String id;

    private Integer order;

    private String chinese;

    private String english;

    private String soundmark;

    private String courseId;
}
