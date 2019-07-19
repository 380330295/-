package com.xuecheng.manage_course.controller;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xuecheng.api.system.SysDictionaryControllerApi;

/**
 * @author Kiku
 * @date 2019/7/4 17:00
 */
@RestController
@RequestMapping("/sys")
public class SysDictionaryController implements SysDictionaryControllerApi {

    @Autowired
    private CourseService courseService;

    @Override
    @GetMapping("dictionary/get/{dType}")
    public SysDictionary getDictionary(@PathVariable(name = "dType")  String dType) {
        return courseService.getDictionaryBydType(dType);
    }



}
