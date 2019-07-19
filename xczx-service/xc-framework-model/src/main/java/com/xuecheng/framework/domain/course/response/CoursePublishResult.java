package com.xuecheng.framework.domain.course.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Kiku
 * @date 2019/7/6 20:50
 */
@NoArgsConstructor
@Data
public class CoursePublishResult extends ResponseResult {
    private String status;

    public CoursePublishResult(ResultCode resultCode ,String status){
        super(resultCode);
        this.status = status;
    }
}
