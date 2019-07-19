package com.xuecheng.manage_course;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.CourseMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Kiku
 * @date 2019/7/6 18:57
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestCourse {

    @Autowired
    CmsPageClient cmsPageClient;
    @Autowired
    CourseMapper courseMapper;

    @Test
    public void testCourseGetUrl(){
        CmsPage cmsPage = cmsPageClient.findById("5d1866d04770884f146dec14");
        cmsPage.setPageAliase("test");
        CmsPageResult result = cmsPageClient.save(cmsPage);
        System.out.println(result);
    }
    @Test
    public void testCourseGetCourseInfo(){
        CourseListRequest courseListRequest = new CourseListRequest();
        courseListRequest.setCompanyId("1");
        Page<CourseInfo> coursePage = courseMapper.findCoursePage(courseListRequest);
        System.out.println(coursePage);
    }
}
