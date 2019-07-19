package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value="CMS配置管理接口",description = "提供数据模型的管理及查询" ,tags = {"CMS配置管理"})
public interface CmsConfigControllerApi {

    @ApiOperation("查询CMS配置信息")
    @ApiImplicitParam(name = "id", value = "主键id", required = true, paramType = "path", dataType = "String")
    public CmsConfig getModel(String id);


}