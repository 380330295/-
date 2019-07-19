package com.xuecheng.manage_cms;

import com.xuecheng.manage_cms.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Kiku
 * @date 2019/6/27 20:08
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsGenerateHtmlTest {

    @Autowired
    PageService pageService;
    @Test
    public void test() {
        pageService.getPageHtml("");

    }

}