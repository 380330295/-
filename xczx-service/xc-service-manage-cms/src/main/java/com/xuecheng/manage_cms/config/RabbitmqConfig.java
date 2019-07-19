package com.xuecheng.manage_cms.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Kiku
 * @date 2019/7/3 10:34
 */
@Configuration
public class RabbitmqConfig {

    //交换机名称
    public static final String EX_ROUTING_CMS_POSTPAGE = "EX_ROUTING_CMS_POSTPAGE";

    /**
     * 声明交换机 EX_ROUTING_CMS_POSTPAGE
     * 交换机配置使用direct类型
     *  @return the exchange
     */
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange EX_ROUTING_CMS_POSTPAGE(){
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }

}
