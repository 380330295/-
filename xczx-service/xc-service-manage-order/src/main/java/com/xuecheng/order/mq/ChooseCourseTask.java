package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.service.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author Kiku
 * @date 2019/7/19 10:57
 */

@Component
public class ChooseCourseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    TaskService taskService;

    //定时向mq发送消息 添加选课任务
    @Scheduled(cron = "0/10 * * * * *") //每10秒发送一次消息
    public void sendChooseCourseTask() {
        //得到1分钟之前的时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.MINUTE, -1);
        Date time = calendar.getTime();
        /*调用service得到一分钟前的 n 条任务*/
        List<XcTask> xcTaskList = taskService.findXcTaskList(time, 100);
        LOGGER.info("get Task size:{}",xcTaskList.size());
        //调用service 发布任务  将添加选课消息发送给mq
        for (XcTask xcTask : xcTaskList) {
            /*通过乐观锁的方式来更新数据表，如果修改结果大于0说明取到任务*/
            if (taskService.getTask(xcTask.getId(), xcTask.getVersion()) > 0) {
                /*发布任务 将添加选课消息发送给mq*/
                taskService.publishTask(xcTask);
                LOGGER.info("send ok...");
            }
        }
    }
    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE) //监听列队
    public void receiveFinishChooseCourseTask(XcTask xcTask){
        if (xcTask!=null&& StringUtils.isNotEmpty(xcTask.getId())){
            taskService.finishTask(xcTask.getId());/*任务完成 执行更新任务操作*/
        }
    }

}
