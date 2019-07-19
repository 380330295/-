package com.xuecheng.search.controller;

import com.xuecheng.api.search.EsCourseControllerApi;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.search.servcie.EsCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Kiku
 * @date 2019/7/9 16:33
 */
@RestController
@RequestMapping("/search/course")
public class EsCourseController implements EsCourseControllerApi {

    @Autowired
    EsCourseService esCourseService;
    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult<CoursePub> queryList(@PathVariable("page") int page,
                                                        @PathVariable("size") int size,
                                                             CourseSearchParam courseSearchParam){
        return esCourseService.queryList(page,size,courseSearchParam);
    }

    @Override
    @GetMapping("/getall/{id}")
    public Map<String, CoursePub> getCourse(@PathVariable("id") String courseId) {
        return esCourseService.getCourseByIds(courseId);
    }
}
