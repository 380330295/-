package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author Kiku
 * @date 2019/7/12 16:19
 */
@Service
public class MediaFileService {
    @Autowired
    MediaFileRepository mediaFileRepository;

    /**
     * 查询媒资文件列表
     *
     * @param page
     * @param size
     * @param params
     * @return
     */
    public QueryResponseResult<MediaFile> queryMediaList(int page, int size, QueryMediaFileRequest params) {
        //条件值对象
        MediaFile mediaFile = new MediaFile();
        if (params == null) {
            params = new QueryMediaFileRequest();
        }
        //文件原始名称
        if (StringUtils.isNotEmpty(params.getFileOriginalName())) {
            mediaFile.setFileOriginalName(params.getFileOriginalName());
        }
        //处理状态
        if (StringUtils.isNotEmpty(params.getProcessStatus())) {
            mediaFile.setProcessStatus(params.getProcessStatus());
        }
        //tag标签
        if (StringUtils.isNotEmpty(params.getTag())) {
            mediaFile.setTag(params.getTag());
        }
        //条件查询匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("tag", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("fileOriginalName", ExampleMatcher.GenericPropertyMatchers.contains());

        //定义Example
        Example<MediaFile> example = Example.of(mediaFile, exampleMatcher);

        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 10;
        }
        page = page - 1;

        Page<MediaFile> mediaFiles = mediaFileRepository.findAll(example, PageRequest.of(page, size));
        QueryResult<MediaFile> queryResult = new QueryResult<>();
        queryResult.setList(mediaFiles.getContent());
        queryResult.setTotal(mediaFiles.getTotalElements());
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }
}
