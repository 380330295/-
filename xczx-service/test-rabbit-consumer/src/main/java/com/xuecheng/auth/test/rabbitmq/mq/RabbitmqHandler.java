package com.xuecheng.auth.test.rabbitmq.mq;

import com.rabbitmq.client.Channel;
import com.xuecheng.auth.test.rabbitmq.config.RabbitmqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author Kiku
 * @date 2019/7/3 12:25
 */
@Component
public class RabbitmqHandler {

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_EMAIL})
    public void send_email(String msg, Message message, Channel channel) {
        System.out.println("receive message is : " + msg);
    }
}
