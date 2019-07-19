package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Kiku
 * @date 2019/7/5 15:47
 */
public interface FileSystemRepository extends MongoRepository<FileSystem,String> {
}
