package com.jimmy.resp;

import lombok.Data;

import java.util.List;

@Data
public class LearningProgressResp {

    private String id;

    private Long userId;

    private String courseId;

    private String statementId;

    private Integer statementIndex;

    private String lastStudyAt;

    private List<String> completedStatement;
}
