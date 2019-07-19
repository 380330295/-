package com.xuecheng.learning.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Kiku
 * @date 2019/7/14 20:33
 */

@FeignClient(value = XcServiceList.XC_SERVICE_SEARCH)
public interface EsMediaClient {

    @GetMapping(value = "/search/getmedia/{teachplanId}")
    public TeachplanMediaPub getMedia(@PathVariable("teachplanId") String teachplanId);

}
