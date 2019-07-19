package com.xuecheng.order.service;

import com.github.pagehelper.PageInfo;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Kiku
 * @date 2019/7/19 10:56
 */
@Service
public class TaskService {

    @Autowired
    XcTaskRepository xcTaskRepository;
    @Autowired
    XcTaskHisRepository xcTaskHisRepository;
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 得到一分钟前的n条任务
     *
     * @param time
     * @param size
     * @return
     */
    public List<XcTask> findXcTaskList(Date time, int size) {
        //设置分页参数
        Pageable pageable = PageRequest.of(0, size);
        //查询前n条任务
        Page<XcTask> tasks = xcTaskRepository.findByUpdateTimeBefore(pageable, time);
        return tasks.getContent();
    }

    /**
     * 通过乐观锁的方式来更新数据表，如果修改结果大于0说明取到任务
     *
     * @return
     */
    @Transactional
    public int getTask(String id, Integer version) {
        if (version == null) {
            version = 1;
        }
        return xcTaskRepository.updateTaskVersion(id, version);
    }

    /**
     * 向mq发送消息 发布添加选课任务
     */
    @Transactional
    public void publishTask(XcTask xcTask) {
        /*查询任务是否存在*/
        Optional<XcTask> optional = xcTaskRepository.findById(xcTask.getId());
        if (optional.isPresent()) {
            XcTask task = optional.get();
            String mqExchange = task.getMqExchange();
            String mqRoutingKey = task.getMqRoutingkey();
            /*向mq发送消息*/
            rabbitTemplate.convertAndSend(mqExchange, mqRoutingKey, xcTask);
            /*更新任务时间*/
            xcTaskRepository.updateTaskTime(task.getId(), new Date());
        }
    }

    /**
     * 完成任务 将数据库任务添加到历史任务
     * @param id
     */
    @Transactional
    public void finishTask(String id) {
        Optional<XcTask> optional = xcTaskRepository.findById(id);
        if (optional.isPresent()){
            XcTask xcTask = optional.get();
            /*删除完成的任务*/
            xcTaskRepository.delete(xcTask);
            /*添加到历史任务*/
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask,xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
        }
    }
}
