package com.xuecheng.manage_course.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Kiku
 * @date 2019/7/6 18:34
 */

@FeignClient(value = XcServiceList.XC_SERVICE_MANAGE_CMS)
public interface CmsPageClient {

    /**
     * 根据页面id 查询页面信息
     * @param id
     * @return
     */
    @GetMapping("/cms/page/get/{id}")
    public CmsPage findById(@PathVariable("id") String id);

    /**
     * 页面保存
     * @param cmsPage
     * @return
     */
    @PostMapping("/cms/page/save")
    public CmsPageResult save(@RequestBody CmsPage cmsPage);


    /**
     * 页面发布接口
     * @param pageId
     * @return
     */
    @PostMapping("/cms/page/postPage/{pageId}")
    public ResponseResult postPage(@PathVariable("pageId") String pageId);

}
