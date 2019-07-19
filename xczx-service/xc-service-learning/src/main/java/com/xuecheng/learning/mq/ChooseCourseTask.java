package com.xuecheng.learning.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.config.RabbitMQConfig;
import com.xuecheng.learning.service.LearningService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Kiku
 * @date 2019/7/19 13:00
 */
@Component
public class ChooseCourseTask {

    @Autowired
    LearningService learningService;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_ADDCHOOSECOURSE) //监听队列
    public void receiveChooseCourseTask(XcTask xcTask){
        //取出队列中消息内容
        String requestBody = xcTask.getRequestBody();
        //解析json
        Map map = JSON.parseObject(requestBody, Map.class);
        /*添加选课*/
        XcLearningCourse xcLearningCourse = new XcLearningCourse();
        xcLearningCourse.setUserId((String) map.get("userId"));
        xcLearningCourse.setCourseId((String) map.get("courseId"));
        ResponseResult result = learningService.addCourse(xcLearningCourse, xcTask);
        if (result.isSuccess()){
            //添加成功向 mq 发送成功消息
            rabbitTemplate.convertAndSend(RabbitMQConfig.EX_LEARNING_ADDCHOOSECOURSE,
                    RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE_KEY,
                    xcTask);
        }
    }
}
