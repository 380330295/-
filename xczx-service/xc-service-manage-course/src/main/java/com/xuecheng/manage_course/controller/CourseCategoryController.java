package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseCategoryControllerApi;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Kiku
 * @date 2019/7/4 14:19
 */
@RestController
@RequestMapping("/course/category")
public class CourseCategoryController implements CourseCategoryControllerApi {
    @Autowired
    private CourseService courseService;

    @Override
    @GetMapping("/list")
    public List<CategoryNode> findCategoryList() {
        return courseService.findCategoryList();
    }
}
