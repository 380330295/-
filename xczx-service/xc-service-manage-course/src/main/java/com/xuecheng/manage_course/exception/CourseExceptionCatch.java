package com.xuecheng.manage_course.exception;

import com.xuecheng.framework.exception.ExceptionCatch;
import com.xuecheng.framework.model.response.CommonCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * @author Kiku
 * @date 2019/7/18 10:56
 */


@ControllerAdvice//控制器增强
@Slf4j
public class CourseExceptionCatch extends ExceptionCatch {
    static {
        builder.put(AccessDeniedException.class, CommonCode.UNAUTHORISE);
    }
}
