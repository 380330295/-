package com.xuecheng.auth.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Kiku
 * @date 2019/7/17 13:49
 */
@FeignClient(XcServiceList.XC_SERVICE_UCENTER)
public interface UcenterClient {

    @GetMapping("/ucenter/getuserext")
    public XcUserExt getUserExt(@RequestParam("username") String username);

}
