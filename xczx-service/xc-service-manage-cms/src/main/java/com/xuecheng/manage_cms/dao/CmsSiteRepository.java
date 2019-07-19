package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Kiku
 * @date 2019/6/27 20:06
 */
public interface CmsSiteRepository extends MongoRepository<CmsSite,String> {
}
