package com.xuecheng.learning.controller;

import com.xuecheng.api.learning.LearningControllerApi;
import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import com.xuecheng.learning.service.LearningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Kiku
 * @date 2019/7/15 12:25
 */
@RestController
@RequestMapping("/learning")
public class LearningController implements LearningControllerApi {

    @Autowired
    private LearningService learningService;

    @Override
    @GetMapping("/getmedia/{courseId}/{teachplanId}")
    public GetMediaResult getMedia(@PathVariable("courseId") String courseId,
                                   @PathVariable("teachplanId") String teachplanId) {
        return learningService.getMedia(courseId,teachplanId);
    }
}
