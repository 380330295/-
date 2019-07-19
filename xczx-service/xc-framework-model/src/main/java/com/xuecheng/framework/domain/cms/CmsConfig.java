package com.xuecheng.framework.domain.cms;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by admin on 2018/2/6.
 */
@Data
@ToString
@Document(collection = "cms_config")
public class CmsConfig {

    @Id
    @ApiModelProperty("主键ID")
    private String id;//主键
    @ApiModelProperty("数据模型的名称")
    private String name;
    @ApiModelProperty("数据模型项目")
    private List<CmsConfigModel> model;

}
