package com.xuecheng.framework.domain.learning.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Kiku
 * @date 2019/7/15 12:19
 */
@Data
@NoArgsConstructor
public class GetMediaResult extends ResponseResult{

    //媒资文件地址
    private String mediaUrl;

    public GetMediaResult(ResultCode resultCode ,String mediaUrl ){
        super(resultCode);
        this.mediaUrl = mediaUrl;
    }

}
