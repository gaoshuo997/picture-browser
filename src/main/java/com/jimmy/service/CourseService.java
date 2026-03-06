package com.jimmy.service;

import com.jimmy.resp.CourseResp;

public interface CourseService {

    CourseResp fetch(String courseId);
}
