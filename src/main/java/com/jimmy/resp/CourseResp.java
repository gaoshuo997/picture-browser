package com.jimmy.resp;

import lombok.Data;

import java.util.List;

@Data
public class CourseResp {

    private Long id;

    private String title;

    private String description;

    private Integer order;

    private List<StatementResp> statements;

    // 已经学完的章节
    private Integer completionCount;

    // 当前学习的章节
    private Integer statementIndex;

    private Long coursePackId;

    // 课程下总共有多少个句子
    private Integer statementCount;
}
