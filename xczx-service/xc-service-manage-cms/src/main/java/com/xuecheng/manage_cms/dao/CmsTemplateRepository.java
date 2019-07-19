package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Kiku
 * @date 2019/6/30 15:10
 */
public interface CmsTemplateRepository  extends MongoRepository<CmsTemplate,String> {
}
