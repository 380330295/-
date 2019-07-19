package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.cms.response.CoursePreviewResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.response.DeleteCourseResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Kiku
 * @date 2019/7/4 9:09
 */
@RestController
@RequestMapping("/course")
public class CourseController extends BaseController implements CourseControllerApi {
    @Autowired
    private CourseService courseService;



    @Override
    @PostMapping("/coursebase/add")
    public AddCourseResult addCourseBase(@RequestBody CourseBase courseBase) {
        return courseService.addCourseBase(courseBase);
    }

    @Override
    @PostMapping("/coursepic/add")
    public AddCourseResult addCoursePic(@RequestParam("courseId") String courseId, @RequestParam("pic")String pic) {
        return courseService.addCoursePic(courseId,pic);
    }

    @Override
    @GetMapping("/coursebase/{courseId}")
    public CourseBase findCourseBaseById(@PathVariable(name = "courseId") String courseId) {
        return courseService.findCourseBaseById(courseId);
    }

    @Override
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic findCoursePicList(@PathVariable("courseId") String courseId) {
        return courseService.findCoursePicList(courseId);
    }

    @Override
    @DeleteMapping("/coursepic/delete")
    public DeleteCourseResult deleteCoursePic(@RequestParam("courseId") String courseId) {
        return courseService.deleteCoursePic(courseId);
    }

    @Override
    @GetMapping("/getCourseMarket/{courseId}")
    public CourseMarket getCourseMarketById(@PathVariable(name = "courseId") String courseId) {
        return courseService.findCourseMarketById(courseId);
    }

    @Override
    @PostMapping("/updateCourse/{courseId}")
    public AddCourseResult updateCourseBase(@PathVariable(name = "courseId") String courseId,
                                            @RequestBody CourseBase courseBase) {
        return courseService.updateCourseBase(courseId,courseBase);
    }

    @Override
    @PostMapping("/updateCourseMarket/{courseId}")
    public AddCourseResult updateCourseMarket(@PathVariable(name = "courseId") String courseId,
                                            @RequestBody CourseMarket courseMarket) {
        return courseService.updateCourseMarket(courseId,courseMarket);
    }

    @Override
    @PreAuthorize("hasAuthority('course_get_baseinfo')")
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachPlanList(@PathVariable(name = "courseId") String courseId) {
        return courseService.findTeachPlanList(courseId) ;
    }

    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {
        return courseService.addTeachplan(teachplan);
    }

    @Override
    @PreAuthorize("hasAuthority('course_find_list')")
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findCourseList(@PathVariable(name = "page") int page,
                                              @PathVariable(name = "size") int size,
                                              CourseListRequest courseListRequest) {
       //利用工具类解析jwt令牌
        XcOauth2Util xcOauth2Util = new XcOauth2Util();
        XcOauth2Util.UserJwt userJwt = xcOauth2Util.getUserJwtFromHeader(request);
        //获取当前用户所属id
        String companyId = userJwt.getCompanyId();
        return courseService.findCourseList(companyId,page,size,courseListRequest);
    }

    @Override
    @GetMapping("/courseview/{courseId}")
    public CourseView findCourseView(@PathVariable(name = "courseId") String courseId) {
        return courseService.findCourseView(courseId);
    }

    @Override
    @PostMapping("/preview/{courseId}")
    public CoursePreviewResult preview(@PathVariable(name = "courseId") String courseId) {
        return courseService.preview(courseId);
    }

    @Override
    @PostMapping("/publish/{courseId}")
    public CoursePublishResult publish(@PathVariable(name = "courseId") String courseId) {
        return courseService.publish(courseId);
    }

    @Override
    @PostMapping("/savemedia")
    public ResponseResult saveMedia(@RequestBody TeachplanMedia teachplanMedia) {
        return courseService.saveMedia(teachplanMedia);
    }
}
