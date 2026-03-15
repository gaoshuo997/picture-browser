package com.jimmy.service;

import com.jimmy.req.CoursePacksSave;
import com.jimmy.resp.CoursePacksResp;

import java.util.List;

public interface CoursePacksService {

    List<CoursePacksResp> list();

    CoursePacksResp fetch(Long id);

    void create(CoursePacksSave save);

    void update(Long id, CoursePacksSave save);
}
