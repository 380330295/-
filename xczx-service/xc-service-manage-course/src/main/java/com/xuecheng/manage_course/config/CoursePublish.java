package com.xuecheng.manage_course.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Kiku
 * @date 2019/7/6 21:24
 */

@Data
@ConfigurationProperties(prefix = "course.publish")
public class CoursePublish {
    private String siteId;
    private String templateId;
    private String previewUrl;
    private String pageWebPath;
    private String pagePhysicalPath;
    private String dataUrlPre;
}
