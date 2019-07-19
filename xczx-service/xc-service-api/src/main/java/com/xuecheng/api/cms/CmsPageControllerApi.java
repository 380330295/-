package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

@Api(value = "页面管理接口", description = "页面管理接口，提供页面的管理及查询" ,tags = {"页面管理"})
public interface CmsPageControllerApi {


    @ApiOperation("查询页面列表(分页)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页记录数", required = true, paramType = "path", dataType = "int")
    })
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);


    @ApiOperation("查询所有站点")
    public ResponseResult findSiteIds();



    @ApiOperation("查询所有模板")
    public ResponseResult findTemplates();



    @ApiOperation("添加页面")
    public CmsPageResult addCmsPage(CmsPage cmsPage);



    @ApiOperation("查询页面信息")
    public CmsPage findById(String id);


    @ApiOperation("修改页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "页面id", required = true, paramType = "path", dataType = "String"),
    })
    public CmsPageResult edit(String id,CmsPage cmsPage);


    @ApiOperation("删除页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "页面id", required = true, paramType = "path", dataType = "String"),
    })
    public ResponseResult delete(String id);



    @ApiOperation("发布页面")
    @ApiImplicitParam(name = "pageId", value = "页面id", required = true, paramType = "path", dataType = "String")
    public ResponseResult post(String pageId);


    @ApiOperation("保存页面")
    public CmsPageResult save(CmsPage cmsPage);
}