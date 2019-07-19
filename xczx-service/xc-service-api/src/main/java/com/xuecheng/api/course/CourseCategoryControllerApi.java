package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * @author Kiku
 * @date 2019/7/4 8:42
 */
@Api(value = "课程分类接口", description = "提供课程分类管理及查询" ,tags = {"课程分类管理"})
public interface CourseCategoryControllerApi {

    @ApiOperation("查询所有课程分类")
    public List<CategoryNode> findCategoryList();

}
