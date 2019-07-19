package com.xuecheng.api.search;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.util.Map;

/**
 * @author Kiku
 * @date 2019/7/9 15:55
 */
@Api(value = "课程搜索", description = "课程搜索", tags = {"课程搜索"})
public interface EsCourseControllerApi {

    @ApiOperation("课程搜索")
    public QueryResponseResult<CoursePub> queryList(int page, int size, CourseSearchParam courseSearchParam);

    @ApiOperation("根据id查询课程信息")
    public Map<String, CoursePub> getCourse(String courseId);
}
