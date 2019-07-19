package com.xuecheng.api.media;

import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Kiku
 * @date 2019/7/12 16:05
 */
@Api(value = "媒资管理接口", description = "提供媒资文件的查询及管理",tags = {"媒资管理接口"})
public interface MediaFileControllerApi {
    @ApiOperation("媒资文件信息查询")
    public QueryResponseResult<MediaFile> mediaList(int page, int size, QueryMediaFileRequest params);
}
