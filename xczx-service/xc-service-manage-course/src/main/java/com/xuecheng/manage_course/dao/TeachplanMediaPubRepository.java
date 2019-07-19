package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.TeachplanMedia;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Kiku
 * @date 2019/7/12 19:17
 */
public interface TeachplanMediaPubRepository extends JpaRepository<TeachplanMediaPub,String> {
    //根据课程id删除课程计划媒资信息
    long deleteByCourseId(String courseId);
}
