package com.xuecheng.api.course;

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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @author Kiku
 * @date 2019/7/4 8:42
 */
@Api(value = "课程管理接口", description = "提供课程信息的管理及查询"  ,tags = {"课程管理"} )
public interface CourseControllerApi {

    @ApiOperation("新增课程")
    @ApiImplicitParam(name = "courseBase", value = "课程基础信息", required = true, paramType = "body", dataType = "CourseBase")
    public AddCourseResult addCourseBase(CourseBase courseBase);

   @ApiOperation("保存课程图片信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true, paramType = "path", dataType = "String"),
    @ApiImplicitParam(name = "pic", value = "图片文件的地址", required = true, paramType = "path", dataType = "String")})
public AddCourseResult addCoursePic(String courseId, String pic);

    @ApiOperation("查询课程基础信息")
    @ApiImplicitParam(name = "courseId", value = "课程id", required = true, paramType = "path", dataType = "String")
    public CourseBase findCourseBaseById(String courseId);

    @ApiOperation("查询课程图片信息")
    @ApiImplicitParam(name = "courseId", value = "课程id", required = true, paramType = "path", dataType = "String")
    public CoursePic findCoursePicList(String courseId);

    @ApiOperation("删除课程图片信息")
    @ApiImplicitParam(name = "courseId", value = "课程id", required = true, paramType = "path", dataType = "String")
    public DeleteCourseResult deleteCoursePic(String courseId);

    @ApiOperation("查询课程营销信息")
    @ApiImplicitParam(name = "courseId", value = "课程id", required = true, paramType = "path", dataType = "String")
    public CourseMarket getCourseMarketById(String courseId);

    @ApiOperation("修改课程基础信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "courseBase", value = "课程基础信息", required = true, paramType = "body", dataType = "CourseBase")})
    public AddCourseResult updateCourseBase(String courseId , CourseBase courseBase);

    @ApiOperation("修改课程营销信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "courseMarket", value = "课程营销信息", required = true, paramType = "body", dataType = "CourseMarket")})
    public AddCourseResult updateCourseMarket(String courseId , CourseMarket courseMarket);

    @ApiOperation("查询课程计划")
    @ApiImplicitParam(name = "courseId", value = "课程id", required = true, paramType = "path", dataType = "String")
    public TeachplanNode findTeachPlanList(String courseId);


    @ApiOperation("添加课程计划")
    @ApiImplicitParam(name = "teachplan", value = "课程计划信息", required = true, paramType = "body", dataType = "Teachplan")
    public ResponseResult addTeachplan(Teachplan teachplan);


    @ApiOperation("查询课程(分页)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页记录数", required = true, paramType = "path", dataType = "int")
    })
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest);


    @ApiOperation("查询课程信息视图")
    public CourseView findCourseView(String courseId);


    @ApiOperation("预览课程")
    @ApiImplicitParam(name = "courseId", value = "课程id", required = true, paramType = "path", dataType = "String")
    public CoursePreviewResult preview(String courseId);

    @ApiOperation("修改课程发布状态")
    @ApiImplicitParam(name = "courseId", value = "课程id", required = true, paramType = "path", dataType = "String")
    public CoursePublishResult publish(String courseId);

    @ApiOperation("选择课程视频")
    public ResponseResult saveMedia(TeachplanMedia teachplanMedia);
}
