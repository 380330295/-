package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Kiku
 * @date 2019/7/6 15:31
 */
@Data
@NoArgsConstructor
@ToString
public class CourseView  implements Serializable {
    private CourseBase courseBase;//课程基本信息
    private CoursePic coursePic; //课程图片信息
    private CourseMarket courseMarket; //课程营销信息
    private TeachplanNode teachplanNode;  //教学计划
}
