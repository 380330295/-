package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Kiku
 * @date 2019/6/30 15:10
 */
public interface CmsConfigRepository extends MongoRepository<CmsConfig,String> {
}
