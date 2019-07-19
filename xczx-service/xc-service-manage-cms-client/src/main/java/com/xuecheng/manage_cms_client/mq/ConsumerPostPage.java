package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms_client.service.PageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Kiku
 * @date 2019/7/3 16:34
 */
@Component
@Slf4j
public class ConsumerPostPage {
    @Autowired
    private PageService pageService;


    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void postPage(String msg) {
        //解析json
        Map map = JSON.parseObject(msg, Map.class);
        log.info("receive cms post page:{} ", map.toString());
        //取出页面id
        String pageId = (String) map.get("pageId");
        if (StringUtils.isEmpty(pageId)) {
        log.error("receive cms pageId is null");
        }
        /*根据页面id查是否存在页面信息 */
        CmsPage cmsPage = pageService.getCmsPageById(pageId);
        if (cmsPage==null){
            log.error("receive post page msg, cmsPage is null , pageId:{}" ,pageId);
            return;
        }
        /*将页面html保存到页面物理路径*/
        pageService.savePageToServerPath(pageId);
    }
}
