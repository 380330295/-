package com.xuecheng.framework.exception;

/**
 * @author Kiku
 * @date 2019/7/1 9:08
 */

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice//控制器增强
@Slf4j
public class ExceptionCatch {
    //使用EXCEPTIONS存放异常类型和错误代码的映射，ImmutableMap的特点的一旦创建不可改变，并且线程安全
    private static ImmutableMap<Class<? extends Throwable>,ResultCode> EXCEPTIONS;
    //使用builder来构建一个异常类型和错误代码的异常
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder = ImmutableMap.builder();

    static {
        builder.put(HttpMessageNotReadableException.class,CommonCode.INVALID_PARAM);
    }

    //捕获 CustomException异常
    @ExceptionHandler(CustomException.class)
    @ResponseBody //json格式返回result
    public ResponseResult customException(CustomException customException){
        customException.printStackTrace();
        log.error("catch exception : {}" ,customException.getResultCode());
        ResultCode resultCode = customException.getResultCode();
        return new ResponseResult(resultCode);
    }


    //捕获不可预知异常
    @ExceptionHandler(Exception.class)
    @ResponseBody //json格式返回result
    public ResponseResult exception(Exception exception){
        exception.printStackTrace();
        log.error("catch exception : {}" ,exception.getMessage());
        if (EXCEPTIONS==null){
            EXCEPTIONS = builder.build();
        }

        final ResultCode resultCode = EXCEPTIONS.get(exception.getClass());

        final ResponseResult responseResult;
        if (resultCode!=null){
            responseResult = new ResponseResult(resultCode);
        }else {
            responseResult = new ResponseResult(CommonCode.SERVER_ERROR);
        }

        return responseResult;
    }
}
