package com.jimmy.resp;

import lombok.Data;

import java.util.List;

@Data
public class LearningProgressResp {

    private Long id;

    private Long userId;

    private Long courseId;

    private String courseTitle;

    private Long statementId;

    private Integer statementIndex;

    private String lastStudyAt;

    private List<Long> completedStatement;

    private Long countStatementByCourse;
}
