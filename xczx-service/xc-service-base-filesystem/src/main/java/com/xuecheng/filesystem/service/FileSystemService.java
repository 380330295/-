package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Kiku
 * @date 2019/7/5 15:40
 */
@Service
@Slf4j
public class FileSystemService {

    @Autowired
    private FileSystemRepository fileSystemRepository;
    @Autowired
    private FastFileStorageClient storageClient;

    /**
     * 文件上传
     *
     * @param multipartFile 文件源
     * @param businesskey   业务key
     * @param filetag       业务标签
     * @param metadata      文件元信息
     * @return
     */
    public UploadFileResult uploadFile(MultipartFile multipartFile, String businesskey, String filetag, String metadata) {
        if (multipartFile == null || multipartFile.getSize() <= 0) { //文件源信息校验
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        //TODO 文件内型校验 <暂时无需求>


        //上传文件到FastDFS 并获取文件路径id
        String storePath = fastUpload(multipartFile);
        if (StringUtils.isEmpty(storePath)) {
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        //拼接完整访问路径
        String path = "http://img.xuecheng.com/" + storePath;


        //设置数据
        FileSystem fileSystem = new FileSystem();

        fileSystem.setFileId(storePath);//使用上传文件storePath 文件路径id 为FileId
        fileSystem.setFileName(multipartFile.getOriginalFilename()); //文件名称
        fileSystem.setFilePath(path); //文件请求路径
        fileSystem.setFileSize(multipartFile.getSize());//文件大小
        fileSystem.setFileType(multipartFile.getContentType());//文件类型
        fileSystem.setBusinesskey(businesskey); //业务key
        fileSystem.setFiletag(filetag); //业务标签
        try {
            //将 metadata 文件元信息 转换为map 存储
            Map map = JSON.parseObject(metadata, Map.class);
            fileSystem.setMetadata(map);//文件元信息
        } catch (Exception e) {
            log.error("json parseObject error JSON:{}" , metadata);
        }


        //存储信息到mongodb 的文件系统数据库中
        FileSystem save = fileSystemRepository.save(fileSystem);

        return new UploadFileResult(CommonCode.SUCCESS, save);
    }

    /**
     * 上传文件到FastDFS
     *
     * @param multipartFile 文件
     * @return 返回文件路径id
     */
    private String fastUpload(MultipartFile multipartFile) {

        if (multipartFile == null || multipartFile.getSize() <= 0) { //文件源校验
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        String path = null;
        try {
            //获取文件拓展名   原始名称截取末尾 "."
            String extension = StringUtils.substringAfterLast(multipartFile.getOriginalFilename(), ".");
            //获取文件流
            InputStream inputStream = multipartFile.getInputStream();
            //获取文件大小
            long fileSize = multipartFile.getSize();
            //上传 并获取storage存储路径
            StorePath storePath = storageClient.uploadFile(inputStream, fileSize, extension, null);
            //拼接完整路径
            path = storePath.getFullPath();

        } catch (Exception e) {
            log.error("file upload error :{}", e);
        }

        return path;
    }
}
