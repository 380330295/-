package com.xuecheng.framework.domain.course.response;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.ResultCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.ToString;


/**
 * Created by admin on 2018/3/5.
 */
@ToString
public enum CourseCode implements ResultCode {
    COURSE_DENIED_DELETE(false,31001,"删除课程失败，只允许删除本机构的课程！"),
    COURSE_PIC_DELETE_ISNULL(false,32001,"删除课程图片失败，该图片信息已经被删除过请刷新后查看！"),
    COURSE_PIC_DELETE(false,32002,"删除课程图片失败,请稍后重试"),
    COURSE_PUBLISH_PERVIEWISNULL(false,31002,"还没有进行课程预览！"),
    COURSE_PUBLISH_CDETAILERROR(false,31003,"创建课程详情页面出错！"),
    COURSE_PUBLISH_COURSEIDISNULL(false,31004,"课程Id为空！"),
    COURSE_PUBLISH_VIEWERROR(false,31005,"发布课程视图出错！"),
    TEACH_PLAN_ISNULL(false,31006,"课程计划没有查询到！"),
    COURSE_ISNULL(false,31010,"课程信息没有查询到！"),
    COURSE_UPDATE_ERROR(false,31007,"课程更新失败！"),
    CATEGORY_ISNULL(false,31008,"课程分类查询为空！"),
    COURSE_VIEW_ISNULL(false,31012,"课程视图信息没查询到！"),
    COURSE_MEDIA_URLISNULL(false,31101,"选择的媒资文件访问地址为空！"),
    COURSE_MEDIA_TEACHPLAN_ISNULL(false,31103,"查询到课程计划不存在！"),
    COURSE_MEDIA_NAMEISNULL(false,31102,"选择的媒资文件名称为空！");

    //操作代码
    @ApiModelProperty(value = "操作是否成功", example = "true", required = true)
    boolean success;

    //操作代码
    @ApiModelProperty(value = "操作代码", example = "22001", required = true)
    int code;
    //提示信息
    @ApiModelProperty(value = "操作提示", example = "操作过于频繁！", required = true)
    String message;
    private CourseCode(boolean success, int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }
    private static final ImmutableMap<Integer, CourseCode> CACHE;

    static {
        final ImmutableMap.Builder<Integer, CourseCode> builder = ImmutableMap.builder();
        for (CourseCode commonCode : values()) {
            builder.put(commonCode.code(), commonCode);
        }
        CACHE = builder.build();
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
