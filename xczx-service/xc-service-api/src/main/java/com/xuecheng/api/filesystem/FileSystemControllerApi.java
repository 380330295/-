package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Kiku
 * @date 2019/7/5 15:27
 */
@Api(value = "文件系统接口", description = "提供文件的管理",tags = {"文件管理"})
public interface FileSystemControllerApi {

    @ApiOperation("文件上传")
    public UploadFileResult uploadFile(MultipartFile multipartFile,
                                        String businesskey,
                                        String filetag,
                                        String metadata);
}
