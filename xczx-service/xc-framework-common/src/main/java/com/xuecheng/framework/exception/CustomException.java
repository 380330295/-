package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author Kiku
 * @date 2019/7/1 9:01
 */
@Data
@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private ResultCode resultCode;
}
