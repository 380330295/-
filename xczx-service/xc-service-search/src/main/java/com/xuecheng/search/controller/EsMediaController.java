package com.xuecheng.search.controller;

import com.xuecheng.api.search.EsMediaControllerApi;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.search.servcie.EsMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Kiku
 * @date 2019/7/14 18:06
 */
@RestController
@RequestMapping("/search")
public class EsMediaController implements EsMediaControllerApi {
    /**
     * 根据课程计划查询媒资信息
     *
     * @param teachplanId
     * @return
     */
    @Autowired
    EsMediaService esMediaService;

    @Override
    @GetMapping(value = "/getmedia/{teachplanId}")
    public TeachplanMediaPub getMedia(@PathVariable("teachplanId") String teachplanId) {
        String[] teachplanIds = {teachplanId};
        QueryResponseResult<TeachplanMediaPub> queryResult = esMediaService.getmedia(teachplanIds);
        if (queryResult != null &&
                queryResult.getQueryResult().getList() != null &&
                queryResult.getQueryResult().getList().size() > 0) {
            return queryResult.getQueryResult().getList().get(0);
        }
        return new TeachplanMediaPub();
    }
}
