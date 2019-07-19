package com.xuecheng.api.learning;

import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Kiku
 * @date 2019/7/15 12:09
 */
@Api(value = "学习中心接口", description = "提供学习中心所需服务",tags = {"学习中心接口"})
public interface LearningControllerApi {

    @ApiOperation("获取视频播放地址")
    public GetMediaResult getMedia(String courseId , String teachplanId);
}
