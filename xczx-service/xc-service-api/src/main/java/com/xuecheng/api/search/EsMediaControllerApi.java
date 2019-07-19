package com.xuecheng.api.search;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Kiku
 * @date 2019/7/14 18:03
 */
@Api(value = "媒资信息搜索", description = "媒资信息搜索", tags = {"媒资信息搜索"})
public interface EsMediaControllerApi {

    @ApiOperation("根据课程计划查询媒资信息")
    public TeachplanMediaPub getMedia(String teachplanId);
}
