package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 */

@RestController
@RequestMapping("cms/page")
public class CmsPageController implements CmsPageControllerApi{

    @Autowired
    private PageService pageService;

    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size")int size, QueryPageRequest queryPageRequest) {
        return pageService.findList(page,size,queryPageRequest);
    }
    @Override
    @GetMapping("/siteIds")
    public ResponseResult findSiteIds() {
        return pageService.findSiteIds();
    }

    @Override
    @GetMapping("/templates")
    public ResponseResult findTemplates() {
        return pageService.findTemplates();
    }

    @Override
    @PostMapping("/add")
    public CmsPageResult addCmsPage(@RequestBody CmsPage cmsPage) {
        return pageService.addCmsPage(cmsPage);
    }

    @Override
    @GetMapping("/get/{id}")
    public CmsPage findById(@PathVariable("id") String id) {
        return pageService.findById(id);
    }

    @Override
    @PutMapping("/edit/{id}")
    public CmsPageResult edit(@PathVariable("id") String id, @RequestBody CmsPage cmsPage) {
        return pageService.update(id,cmsPage);
    }

    @Override
    @DeleteMapping("/del/{id}")
    public ResponseResult delete(@PathVariable("id") String id) {
        return pageService.del(id);
    }

    @Override
    @PostMapping("/postPage/{pageId}")
    public ResponseResult post(@PathVariable("pageId") String pageId) {
        return pageService.postPage(pageId);
    }

    @Override
    @PostMapping("/save")
    public CmsPageResult save(@RequestBody CmsPage cmsPage) {
        return pageService.save(cmsPage);
    }
}