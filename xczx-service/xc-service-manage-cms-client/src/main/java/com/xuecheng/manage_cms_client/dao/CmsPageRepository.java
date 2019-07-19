package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Kiku
 * @date 2019/6/27 20:06
 */
public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
}
