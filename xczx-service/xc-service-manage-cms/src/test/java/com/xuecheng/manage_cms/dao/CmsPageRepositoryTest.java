package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Kiku
 * @date 2019/6/27 20:08
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
     CmsPageRepository cmsPageRepository;
    @Test
    public void testFindAll(){
        List<CmsPage> all = cmsPageRepository.findAll();

    }

    @Test
    public void testFindPage(){
        int page = 0; //从0开始
        int size = 5;
        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);


    }
    //条件查询
    @Test
    public void testFindExample(){
        int page = 0; //从0开始
        int size = 5;
        //设置分页
        Pageable pageable = PageRequest.of(page,size);
        //条件值对象
        CmsPage cmsPage = new CmsPage();
        //设置条件
        cmsPage.setPageAliase("轮播");
        //条件匹配器设置
        ExampleMatcher exampleMatcher = ExampleMatcher.matching() //默认匹配
                .withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains()); //包含匹配

        //定义Example
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        List<CmsPage> content = all.getContent();
        System.out.println(content);


    }

}