package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.*;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.StringContent;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 页面管理服务
 * @author Kiku
 */
@Service
public class PageService {

    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;
    @Autowired
    private PageService pageService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;
    @Autowired
    private CmsConfigRepository cmsConfigRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 根据id查询CmsConfig
     *
     * @param id
     * @return
     */
    public CmsConfig findCmsConfigById(String id) {
        if (StringUtils.isEmpty(id)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<CmsConfig> optional = cmsConfigRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }


    /**
     * 页面信息查询
     * @param page             页码 从1开始
     * @param size             每页显示条数
     * @param queryPageRequest 查询条件
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        //条件值对象
        CmsPage cmsPage = new CmsPage();
        //设置条件
        if (queryPageRequest == null)
            queryPageRequest = new QueryPageRequest();
        //别名
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase()))
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        //站点id
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId()))
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        //模版id
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId()))
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());

        //条件查询匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());

        //定义Example
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);

        if (page <= 0)
            page = 1;
        if (size < 0)
            size = 10;
        page = page - 1;
        Page<CmsPage> all = cmsPageRepository.findAll(example, PageRequest.of(page, size));
        QueryResult<CmsPage> queryResult = new QueryResult<>();
        queryResult.setList(all.getContent());
        queryResult.setTotal(all.getTotalElements());
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }

    /**
     * 查询所有站点
     *
     * @return
     */
    public ResponseResult findSiteIds() {
        List<CmsSite> all = cmsSiteRepository.findAll();
        if (CollectionUtils.isEmpty(all)) { //查询为空
            ExceptionCast.cast(CmsCode.CMS_PAGE_SITEISNULL);
        }
        QueryResult<CmsSite> queryResult = new QueryResult<>();
        queryResult.setList(all);
        queryResult.setTotal(all.size());
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }


    /**
     * 查询所有模板
     *
     * @return
     */
    public ResponseResult findTemplates() {
        List<CmsTemplate> all = cmsTemplateRepository.findAll();
        if (CollectionUtils.isEmpty(all)) { //查询为空
            ExceptionCast.cast(CmsCode.CMS_PAGE_TEMPLATEISNULL);
        }
        QueryResult<CmsTemplate> queryResult = new QueryResult<>();
        queryResult.setList(all);
        queryResult.setTotal(all.size());
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }

    /**
     * 添加cms页面
     *
     * @param cmsPage
     * @return
     */
    public CmsPageResult addCmsPage(CmsPage cmsPage) {
        if (cmsPage == null) { //参数校验
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //根据页面名称、站点id、页面访问路径查询页面是否存在
        CmsPage findCmsPage = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(),
                cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (findCmsPage != null) { //存在
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }

        cmsPage.setPageId(null);  //添加页面主键由spring data 自动生成
        CmsPage save = cmsPageRepository.save(cmsPage);

        return new CmsPageResult(CommonCode.SUCCESS, save);

    }


    /**
     * 修改Cms页面
     *
     * @param id
     * @param cmsPage
     * @return
     */
    public CmsPageResult update(String id, CmsPage cmsPage) {
        if (cmsPage == null || StringUtils.isEmpty(id)) { //参数校验
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        CmsPage one = this.findById(id); //根据id 查询cmsPage
        if (one == null) { //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }

        //更新模板id
        one.setTemplateId(cmsPage.getTemplateId());
        //更新所属站点
        one.setSiteId(cmsPage.getSiteId());
        //更新页面别名
        one.setPageAliase(cmsPage.getPageAliase());
        //更新页面名称
        one.setPageName(cmsPage.getPageName());
        // 更新访问路径
        one.setPageWebPath(cmsPage.getPageWebPath());
        // 更新物理路径
        one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
        //更新dataUrl
        one.setDataUrl(cmsPage.getDataUrl());
        // 执行更新
        CmsPage save = cmsPageRepository.save(one);
        return new CmsPageResult(CommonCode.SUCCESS, save);
    }


    /**
     * 根据页面id 查询CmsPage
     *
     * @param id
     * @return
     */
    public CmsPage findById(String id) {
        if (StringUtils.isEmpty(id)) { //校验参数
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (!optional.isPresent()) { //如果查询结果为空
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        return optional.get();

    }

    /**
     * 根据页面id 删除页面
     *
     * @param id
     * @return
     */
    public ResponseResult del(String id) {
        if (StringUtils.isEmpty(id)) {
            //参数校验
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //先查询
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (!optional.isPresent()) {//页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        cmsPageRepository.deleteById(id);
        return new ResponseResult(CommonCode.SUCCESS);
    }


    /**
     * 页面静态化  3步 :
     * 1.获取页面模型数据 + 2.获取页面模板-->  3.执行静态化
     *
     * @param pageId
     * @return
     */

    public String getPageHtml(String pageId) {
        //获取页面模型数据
        Map model = this.getModelByPageId(pageId);
        if (model == null) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //获取页面模板
        String template = this.getTemplateByPageId(pageId);
        if (template == null) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_TEMPLATEISNULL);
        }
        //执行静态化
        String html = this.generateHtml(template, model);
        if (StringUtils.isEmpty(html)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        return html;
    }


    //获取页面模型数据
    private Map getModelByPageId(String pageId) {
        //参数校验
        if (StringUtils.isEmpty(pageId)) {//参数为空
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //根据页面id 查询页面信息
        CmsPage cmsPage = pageService.findById(pageId);
        if (cmsPage == null) {//页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //取出dataUrl
        String dataUrl = cmsPage.getDataUrl();
        //校验dataUrl是否为空
        if (StringUtils.isEmpty(dataUrl)) {//dataUrl为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //RestTemplate 获取Map格式数据
        ResponseEntity<Map> entity = restTemplate.getForEntity(dataUrl, Map.class);
        //Map body = entity.getBody();
        return entity.getBody();
    }

    //获取页面模板
    private String getTemplateByPageId(String pageId) {
        //参数校验
        if (StringUtils.isEmpty(pageId)) {//参数为空
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //根据页面id 查询页面信息
        CmsPage cmsPage = pageService.findById(pageId);
        if (cmsPage == null) {//页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //页面模板id
        String templateId = cmsPage.getTemplateId();
        if (StringUtils.isEmpty(templateId)) {//页面模板为空
            ExceptionCast.cast(CmsCode.CMS_PAGE_TEMPLATEISNULL);
        }
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if (optional.isPresent()) {
            //页面模板
            CmsTemplate cmsTemplate = optional.get();
            //获取文件模板id
            String templateFileId = cmsTemplate.getTemplateFileId();
            //取出模板文件内容
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //打开下载流对象
            if (gridFSFile == null) {
                ExceptionCast.cast(CmsCode.CMS_PAGE_TEMPLATE_FILE_ISNULL);
            }
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建GridFsResource
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
            try {
                return IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //页面模板静态化
    private String generateHtml(String template, Map model) {
        if (StringUtils.isEmpty(template) || model == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        try {
            //生成配置类
            Configuration configuration = new Configuration(Configuration.getVersion());
            //模板加载器
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            stringTemplateLoader.putTemplate("template", template);
            //配置模板加载器
            configuration.setTemplateLoader(stringTemplateLoader);
            //获取模板
            Template temp = configuration.getTemplate("template");
            //静态化 并返回
            return FreeMarkerTemplateUtils.processTemplateIntoString(temp, model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 页面发布
     *
     * @param pageId
     * @return
     */
    public ResponseResult postPage(String pageId) {
        //页面静态化
        String pageHtml = this.getPageHtml(pageId);
        //将静态化文件保存到GridFS中
        CmsPage cmsPage = this.saveHtml(pageId, pageHtml);
        //给mq列队发送消息
        this.sendPostPage(pageId);

        return new ResponseResult(CommonCode.SUCCESS);
    }


    /**
     * 将静态化html保存到GridFS中
     * @param pageId
     * @param htmlContent
     * @return
     */
    private CmsPage saveHtml(String pageId, String htmlContent) {
        if (StringUtils.isEmpty(pageId)||StringUtils.isEmpty(htmlContent)){ //参数校验
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        CmsPage cmsPage = this.findById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        ObjectId objectId = null;
        InputStream inputStream = null;
        try {
            //获取输入流
            inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
            //将html文件保存到GridFS中
            objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //将html文件id 更新到cmsPage中
        cmsPage.setHtmlFileId(objectId.toHexString());
        cmsPage = cmsPageRepository.save(cmsPage);
        return cmsPage;
    }


    /**
     * 给mq列队发送消息
     * @param pageId
     */
    private  void sendPostPage(String pageId){
        if (StringUtils.isEmpty(pageId)){ //参数校验
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        CmsPage cmsPage = this.findById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //创建消息对象
        Map<String,String> msg = new HashMap<String,String>();
        msg.put("pageId",pageId);
        //转换为Json
        String jsonString = JSON.toJSONString(msg);
        //给mq列队发送消息
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,cmsPage.getSiteId(),jsonString);
    }

    /**
     * 保存页面
     * 查询数据库中 有则更新 无则添加
     * @param cmsPage
     * @return
     */
    public CmsPageResult save(CmsPage cmsPage) {
        //根据页面名称、站点id、页面访问路径查询页面是否存在
        CmsPage findCmsPage = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(),
                cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (findCmsPage != null) {
            //存在  更新
            CmsPageResult result = this.update(findCmsPage.getPageId(), cmsPage);
            return new CmsPageResult(CommonCode.SUCCESS,result.getCmsPage());
        }else {
            // 添加
            CmsPageResult result = this.addCmsPage(cmsPage);
            return new CmsPageResult(CommonCode.SUCCESS,result.getCmsPage());
        }
    }
}
