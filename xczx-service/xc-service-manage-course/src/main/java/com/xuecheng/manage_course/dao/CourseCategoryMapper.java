package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by Administrator.
 */
@Mapper
public interface CourseCategoryMapper {
   List<CategoryNode> findCategoryList();
}
