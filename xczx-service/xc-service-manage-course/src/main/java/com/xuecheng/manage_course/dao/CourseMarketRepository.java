package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Administrator.
 */
@Mapper
public interface CourseMarketRepository extends JpaRepository<CourseMarket,String> {

}
