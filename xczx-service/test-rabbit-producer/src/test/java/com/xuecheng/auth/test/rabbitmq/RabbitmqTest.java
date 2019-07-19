package com.xuecheng.auth.test.rabbitmq;

import com.xuecheng.auth.test.rabbitmq.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Kiku
 * @date 2019/7/3 12:16
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RabbitmqTest {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void testSendByTopics(){
        for (int i = 0; i < 5; i++) {
            String message = "email inform to user" + i;

            rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM,"inform.email",message);

            System.out.println("send Message is :"+"'"+message+"'" );
        }
    }
}
