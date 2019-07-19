package com.xuecheng.manage_media.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-07-12 9:04
 **/
@Configuration
public class RabbitMQConfig {

    //交换机
    public static final String EX_MEDIA_PROCESSTASK = "ex_media_processor";


    /**
     * 声明交换机 ex_media_processor
     * 交换机配置使用direct类型
     * @return the exchange
     */
    @Bean(EX_MEDIA_PROCESSTASK)
    public Exchange EX_MEDIA_PROCESSTASK() {
        return ExchangeBuilder.directExchange(EX_MEDIA_PROCESSTASK).durable(true).build();
    }

}
