package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Kiku
 * @date 2019/7/10 15:50
 */
@Api(value = "媒资文件上传接口", description = "提供媒资文件的上传",tags = {"媒资文件上传接口"})
public interface MediaUploadControllerApi {

    @ApiOperation("文件上传注册")
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimeType, String fileExt);

    @ApiOperation("文件分块上传")
    public ResponseResult uploadChunk(MultipartFile file, Integer chunk, String fileMd5);

    @ApiOperation("文件分块校验")
    public CheckChunkResult checkChunk(String fileMd5, Integer chunk, Long chunkSize);

    @ApiOperation("合并文件")
    public ResponseResult mergeChunks(String fileMd5, String fileName, Long fileSize, String mimeType, String fileExt);
}
