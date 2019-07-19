package com.xuecheng.manage_cms_client.config;

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
    //队列bean名称
    public static final String QUEUE_CMS_POSTPAGE = "queue_cms_postpage";
    //交换机名称
    public static final String EX_ROUTING_CMS_POSTPAGE = "EX_ROUTING_CMS_POSTPAGE";
    //列队名字
    @Value("${xuecheng.mq.queue}")
    public String queue_cms_postpage_name;
    //routingKey
    @Value("${xuecheng.mq.routingKey.portal}")
    public String portal;
    //routingKey
    @Value("${xuecheng.mq.routingKey.course}")
    public String course;

    /**
     * 声明交换机 EX_ROUTING_CMS_POST_PAGE
     * 交换机配置使用direct类型
     *  @return the exchange
     */
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange EX_ROUTING_CMS_POSTPAGE(){
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }
    //声明队列 QUEUE_CMS_POSTPAGE
    @Bean(QUEUE_CMS_POSTPAGE)
    public Queue QUEUE_CMS_POSTPAGE(){
        return new Queue(queue_cms_postpage_name);
    }

    // 绑定交换机和队列 指定routingKey
    @Bean
    public Binding BINDING_QUEUE_SAVE_PORTAL(@Qualifier (QUEUE_CMS_POSTPAGE) Queue queue,
                                              @Qualifier (EX_ROUTING_CMS_POSTPAGE) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(portal).noargs();
    }
  // 绑定交换机和队列 指定routingKey
    @Bean
    public Binding BINDING_QUEUE_SAVE_COURSE(@Qualifier (QUEUE_CMS_POSTPAGE) Queue queue,
                                              @Qualifier (EX_ROUTING_CMS_POSTPAGE) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(course).noargs();
    }


}
