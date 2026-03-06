package com.jimmy.resp;

import lombok.Data;

@Data
public class StatementResp {

    private Long id;

    private Integer order;

    private String chinese;

    private String english;

    private String soundmark;

    private Long courseId;
}
