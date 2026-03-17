package com.jimmy.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseStateCountDTO {

    private Long courseId;

    private Long statementCount = 0L;
}
