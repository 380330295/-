package com.xuecheng.filesystem.controller;

import com.xuecheng.api.filesystem.FileSystemControllerApi;
import com.xuecheng.filesystem.service.FileSystemService;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Kiku
 * @date 2019/7/5 15:37
 */
@RestController
@RequestMapping("/filesystem")
public class FileSystemController implements FileSystemControllerApi {

    @Autowired
    FileSystemService fileSystemService;

    @Override
    @PostMapping("/upload")
    public UploadFileResult uploadFile(@RequestParam("file") MultipartFile multipartFile,
                                       @RequestParam("businesskey")String businesskey,
                                       @RequestParam("filetag")String filetag,
                                       @RequestParam("metadata")String metadata) {
        return fileSystemService.uploadFile(multipartFile,businesskey,filetag,metadata);
    }
}
