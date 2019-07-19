package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Kiku
 * @date 2019/7/17 13:24
 */
@Api(value = "用户中心接口" ,description = "提供用户中心管理服务")
public interface UcenterControllerApi {

    @ApiOperation("获取用户信息")
    public XcUserExt getUserExt(String username);


}
