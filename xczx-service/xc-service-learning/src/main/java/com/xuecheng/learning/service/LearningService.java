package com.xuecheng.learning.service;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import com.xuecheng.framework.domain.learning.response.LearningCode;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.client.EsMediaClient;
import com.xuecheng.learning.dao.XcLearningCourseRepository;
import com.xuecheng.learning.dao.XcTaskHisRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author Kiku
 * @date 2019/7/14 20:44
 */
@Service
public class LearningService {

    @Autowired
    private EsMediaClient esMediaClient;
    @Autowired
    XcLearningCourseRepository xcLearningCourseRepository;
    @Autowired
    XcTaskHisRepository xcTaskHisRepository;

    /**
     * 获取课程学习地址
     *
     * @param courseId
     * @param teachplanId
     * @return
     */
    public GetMediaResult getMedia(String courseId, String teachplanId) {
        //TODO 学生学习权限校验

        /*远程调用搜索服务查询课程计划对应的课程媒资信息*/
        TeachplanMediaPub teachplanMediaPub = esMediaClient.getMedia(teachplanId);
        if (teachplanMediaPub == null || StringUtils.isEmpty(teachplanMediaPub.getMediaUrl())) {
            //获取学习地址错误
            ExceptionCast.cast(LearningCode.LEARNING_GET_MEDIA_ERROR);
        }
        return new GetMediaResult(CommonCode.SUCCESS, teachplanMediaPub.getMediaUrl());
    }


    /**
     * 添加选课
     *
     * @param xcLearningCourse
     * @param xcTask
     * @return
     */
    @Transactional
    public ResponseResult addCourse(XcLearningCourse xcLearningCourse, XcTask xcTask) {
        if (xcLearningCourse == null ||
                StringUtils.isEmpty(xcLearningCourse.getUserId()) ||
                StringUtils.isEmpty(xcLearningCourse.getCourseId())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        /*查询选课是否已经添加*/
        XcLearningCourse one = xcLearningCourseRepository.
                findByUserIdAndCourseId(xcLearningCourse.getUserId(), xcLearningCourse.getCourseId());
        if (one == null) {
            /*添加选课*/
            xcLearningCourseRepository.save(xcLearningCourse);
        }
        Optional<XcTaskHis> optional = xcTaskHisRepository.findById(xcTask.getId());
        if (!optional.isPresent()) {
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask, xcTaskHis);
            /*添加完成历史任务*/
            xcTaskHisRepository.save(xcTaskHis);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
