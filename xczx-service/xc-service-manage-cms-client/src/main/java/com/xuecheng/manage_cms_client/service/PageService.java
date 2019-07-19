package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

/**
 * @author Kiku
 * @date 2019/7/3 15:13
 */
@Service
@Slf4j
public class PageService {
    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    CmsSiteRepository cmsSiteRepository;

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    /**
     * 将页面html保存到页面物理路径
     *
     * @param pageId
     */

    public void savePageToServerPath(String pageId) {
        //根据pageId获取cms页面信息
        CmsPage cmsPage = this.getCmsPageById(pageId);
        //根据站点id获得站点信息
        String siteId = cmsPage.getSiteId();
        CmsSite cmsSite = this.getCmsSiteById(siteId);
        //根据文件id获取文件内容
        String fileId = cmsPage.getHtmlFileId();
        InputStream inputStream = this.getFileById(fileId);
        if (inputStream == null) {
            log.error("cms generate html is null , fileId:{}", fileId);
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }

        // 路径:  站点物理路径 +页面物理路径
        String path = cmsSite.getSitePhysicalPath() + cmsPage.getPagePhysicalPath();
        if (new File(path).mkdirs()) { //如果路径不存在 则自动创建
            log.info("add path -> path:{}", path);
        }
        //页面物理路径=站点物理路径+页面物理路径+页面名称。
        String pagePath = path + cmsPage.getPageName();

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(pagePath);
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 根据页面id 获取页面信息
     *
     * @param pageId
     * @return
     */
    public CmsPage getCmsPageById(String pageId) {
        if (StringUtils.isEmpty(pageId)) { //参数为空
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        return optional.get();
    }

    /**
     * 根据站点id获得站点信息
     *
     * @param siteId
     * @return
     */
    public CmsSite getCmsSiteById(String siteId) {
        if (StringUtils.isEmpty(siteId)) { //参数为空
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_SITEISNULL);
        }
        return optional.get();
    }


    /**
     * 根据文件id获取文件内容
     *
     * @param fileId
     * @return
     */

    public InputStream getFileById(String fileId) {
        if (StringUtils.isEmpty(fileId)) { //参数为空
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建GridFsResource
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        try {
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
