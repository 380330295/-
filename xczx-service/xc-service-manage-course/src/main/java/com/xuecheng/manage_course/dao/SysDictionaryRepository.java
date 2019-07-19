package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Administrator.
 */
public interface SysDictionaryRepository extends MongoRepository<SysDictionary,String> {
    public SysDictionary findBydType (String dType);
}
