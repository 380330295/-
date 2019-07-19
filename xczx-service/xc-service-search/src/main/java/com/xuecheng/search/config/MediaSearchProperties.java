package com.xuecheng.search.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Kiku
 * @date 2019/7/9 16:31
 */
@Data
@ConfigurationProperties(prefix = "xuecheng.media")
public class MediaSearchProperties {
    private String index; //索引
    private String type; //类型
    private String[] sourceField;//source过滤字段
}
