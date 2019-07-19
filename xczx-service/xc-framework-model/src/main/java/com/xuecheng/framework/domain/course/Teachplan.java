package com.xuecheng.framework.domain.course;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by admin on 2018/2/7.
 */
@Data
@ToString
@Entity
@Table(name="teachplan")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class Teachplan implements Serializable {
    private static final long serialVersionUID = -916357110051689485L;
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    @ApiModelProperty("主键id")
    private String id;
    @ApiModelProperty("章节名称")
    private String pname;
    @ApiModelProperty("父章节id")
    private String parentid;
    @ApiModelProperty("层级，分为1、2、3级")
    private String grade;
    @ApiModelProperty("课程类型:1视频、2文档")
    private String ptype;
    @ApiModelProperty("章节及课程时介绍")
    private String description;
    @ApiModelProperty("课程id")
    private String courseid;
    @ApiModelProperty("状态：未发布、已发布")
    private String status;
    @ApiModelProperty("排序字段")
    private Integer orderby;
    @ApiModelProperty("时长，单位分钟")
    private Double timelength;
    @ApiModelProperty("是否试学")
    private String trylearn;


}
