package com.xuecheng.govern.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author Kiku
 * @date 2019/7/6 12:37
 */

@SpringBootApplication
@EnableEurekaServer //标识 是一个Eureka 注册中心
public class GovernCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(GovernCenterApplication.class,args);
    }
}
