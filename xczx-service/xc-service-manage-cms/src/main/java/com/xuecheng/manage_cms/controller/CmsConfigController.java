package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsConfigControllerApi;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Kiku
 * @date 2019/7/1 15:27
 */
@RestController
@RequestMapping("/cms/config")
public class CmsConfigController implements CmsConfigControllerApi {


    @Autowired
    PageService pageService;

    @GetMapping("/getModel/{id}")
    @Override
    public CmsConfig getModel(@PathVariable("id") String id) {
        return pageService.findCmsConfigById(id);
    }
}
