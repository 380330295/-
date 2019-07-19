package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CoursePreviewResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.response.DeleteCourseResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.DateUtils;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.config.CoursePublish;
import com.xuecheng.manage_course.dao.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Kiku
 * @date 2019/7/4 9:05
 */
@Service
@Slf4j
@EnableConfigurationProperties(CoursePublish.class)
public class CourseService {
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private CourseBaseRepository courseBaseRepository;
    @Autowired
    private TeachplanRepository teachplanRepository;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseCategoryMapper courseCategoryMapper;
    @Autowired
    private SysDictionaryRepository sysDictionaryRepository;
    @Autowired
    private CourseMarketRepository courseMarketRepository;
    @Autowired
    private CoursePicRepository coursePicRepository;
    @Autowired
    private CmsPageClient cmsPageClient;
    @Autowired
    private CoursePublish coursePublish;
    @Autowired
    private CoursePubRepository coursePubRepository;
    @Autowired
    private TeachplanMediaRepository teachplanMediaRepository;
    @Autowired
    private TeachplanMediaPubRepository teachplanMediaPubRepository;

    /**
     * 根据课程id 查询课程计划
     *
     * @param courseId
     * @return
     */
    public TeachplanNode findTeachPlanList(String courseId) {
        if (StringUtils.isEmpty(courseId)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        TeachplanNode teachPlanList = teachplanMapper.findTeachPlanList(courseId);
        if (teachPlanList == null) {
            ExceptionCast.cast(CourseCode.TEACH_PLAN_ISNULL);
        }
        return teachPlanList;
    }

    /**
     * 添加课程计划
     *
     * @param teachplan
     * @return
     */
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {
        //参数校验 课程id和课程计划名称
        if (teachplan == null ||
                StringUtils.isEmpty(teachplan.getPname()) ||
                StringUtils.isEmpty(teachplan.getCourseid())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //获取课程id
        String courseId = teachplan.getCourseid();
        //获取父节点id
        String parentId = teachplan.getParentid();
        if (StringUtils.isEmpty(parentId)) {
            //如果父节点id为空 则获取根节点
            parentId = getTeachplanRoot(courseId);
            if (StringUtils.isEmpty(parentId)) {
                log.error("get parentId is null, method : getTeachplanRoot(), param courseId:{}", courseId);
                ExceptionCast.cast(CommonCode.INVALID_PARAM);
            }
        }
        //获取父节点信息
        Optional<Teachplan> optional = teachplanRepository.findById(parentId);
        if (!optional.isPresent()) {
            log.error("get teachplanParent is null , param parentId:{}", parentId);
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //父节点信息
        Teachplan teachplanParent = optional.get();
        //父节点级别
        String grade = teachplanParent.getGrade();

        teachplan.setGrade(Integer.toString(Integer.parseInt(grade) + 1));//设置级别  根据父节点设置
        teachplan.setParentid(parentId);//设置父节点id
        teachplan.setStatus("0");//设置状态 未发布
        teachplan.setCourseid(courseId);//设置课程id

        //添加课程计划
        teachplanRepository.save(teachplan);

        return ResponseResult.SUCCESS();
    }

    //获取课程根结点，如果没有则添加根结点
    private String getTeachplanRoot(String courseId) {
        //根据课程id 查询课程信息
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            log.error("find CourseBase by id result is null, courseId:{}", courseId);
            return null;
        }
        //课程信息
        CourseBase courseBase = optional.get();
        //取出课程计划根结点
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if (CollectionUtils.isEmpty(teachplanList)) {
            //新增一个节点
            Teachplan teachplanRoot = new Teachplan();
            teachplanRoot.setCourseid(courseId); //设置课程id
            teachplanRoot.setPname(courseBase.getName()); //设置课程计划名称
            teachplanRoot.setStatus("0");//未发布
            teachplanRoot.setParentid("0");//根节点
            teachplanRoot.setGrade("1");//1级
            //添加
            Teachplan teachplan = teachplanRepository.save(teachplanRoot);
            //获取保存后的根节点id
            return teachplan.getId();
        }
        //返回根节点id
        return teachplanList.get(0).getId();

    }

    /**
     * 分页查询我的课程
     *
     * @param page 第几页
     * @param size 每页大小
     * @return
     */
    public QueryResponseResult findCourseList(String companyId , int page, int size , CourseListRequest courseListRequest) {

        if (courseListRequest==null){
            courseListRequest =   new CourseListRequest();
        }
        courseListRequest.setCompanyId(companyId);
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 7;
        }
        //分页
        PageHelper.startPage(page, size);
        //查询
        Page<CourseInfo> coursePage = courseMapper.findCoursePage(courseListRequest);
        if (CollectionUtils.isEmpty(coursePage)) {
            ExceptionCast.cast(CourseCode.COURSE_ISNULL);
        }
        QueryResult<CourseInfo> queryResult = new QueryResult<CourseInfo>();
        queryResult.setTotal(coursePage.getTotal());
        queryResult.setList(coursePage.getResult());

        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }

    /**
     * 查询所有课程分类信息
     *
     * @return
     */
    public List<CategoryNode> findCategoryList() {
        //查信息所有分类
        List<CategoryNode> categoryList = courseCategoryMapper.findCategoryList();
        if (CollectionUtils.isEmpty(categoryList)) {
            ExceptionCast.cast(CourseCode.CATEGORY_ISNULL);
        }
        return categoryList;
    }

    /**
     * 查询数据字典
     *
     * @param dType
     * @return
     */
    public SysDictionary getDictionaryBydType(String dType) {
        //参数校验
        if (StringUtils.isEmpty(dType)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        SysDictionary dictionary = sysDictionaryRepository.findBydType(dType);
        if (dictionary == null || CollectionUtils.isEmpty(dictionary.getDValue())) {
            ExceptionCast.cast(CommonCode.FIND_ISNULL);
        }
        return dictionary;
    }

    /**
     * 添加页面基础信息
     *
     * @param courseBase 课程基础信息
     * @return
     */
    @Transactional
    public AddCourseResult addCourseBase(CourseBase courseBase) {
        //参数校验 校验课程名称  课程等级 学习模式
        if (courseBase == null ||
                StringUtils.isEmpty(courseBase.getName()) ||
                StringUtils.isEmpty(courseBase.getGrade()) ||
                StringUtils.isEmpty(courseBase.getStudymodel())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //向数据库添加
        CourseBase save = courseBaseRepository.save(courseBase);
        //获取添加后的课程id
        String courseId = save.getId();

        return new AddCourseResult(CommonCode.SUCCESS, courseId);
    }

    public CourseBase findCourseBaseById(String courseId) {
        if (StringUtils.isEmpty(courseId)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CourseCode.COURSE_ISNULL);
        }
        return optional.get();
    }

    /**
     * 修改课程基本信息
     *
     * @param courseId   课程id
     * @param courseBase 课程基本信息
     * @return
     */
    @Transactional
    public AddCourseResult updateCourseBase(String courseId, CourseBase courseBase) {
        //参数校验 课程id 校验课程名称  课程等级 学习模式
        if (StringUtils.isEmpty(courseId) || courseBase == null ||
                StringUtils.isEmpty(courseBase.getName()) ||
                StringUtils.isEmpty(courseBase.getGrade()) ||
                StringUtils.isEmpty(courseBase.getStudymodel())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //更新数据库
        courseBase.setId(courseId);
        CourseBase save = courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS, save.getId());
    }

    /**
     * 课程营销信息修改
     *
     * @param courseId     课程id
     * @param courseMarket 课程营销信息
     * @return
     */
    @Transactional
    public AddCourseResult updateCourseMarket(String courseId, CourseMarket courseMarket) {
        //参数校验 校验课程id 课程收费规则 课程有效期
        if (StringUtils.isEmpty(courseId) ||
                StringUtils.isEmpty(courseMarket.getCharge()) ||
                StringUtils.isEmpty(courseMarket.getValid())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        courseMarket.setId(courseId);
        //查询课程营销信息是否存在
        CourseMarket findCourseMarket = this.findCourseMarketById(courseId);
        if (findCourseMarket == null) {
            //不存在 直接添加新的课程营销信息
            courseMarketRepository.save(courseMarket);
            return new AddCourseResult(CommonCode.SUCCESS, courseId);
        }
        //如果存在 则修改课程营销信息
        courseMarket.setPrice_old(findCourseMarket.getPrice()); //原价设置为修改前的价格
        if ("203001".equals(courseMarket.getCharge())) {
            //如果是免费课程 将价格设置为0.00元
            courseMarket.setPrice(0.00F);
        }
        if ("204001".equals(courseMarket.getValid())) {
            //如果课程永久有效 则清除课程有效期
            courseMarket.setStartTime(null);
            courseMarket.setEndTime(null);
        }
        courseMarketRepository.save(courseMarket);
        return new AddCourseResult(CommonCode.SUCCESS, courseId);
    }


    /**
     * 课程营销信息查询
     *
     * @param courseId 课程id
     * @return
     */
    public CourseMarket findCourseMarketById(String courseId) {
        //参数校验
        if (StringUtils.isEmpty(courseId)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //查询
        Optional<CourseMarket> optional = courseMarketRepository.findById(courseId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    /**
     * 课程图片保存
     *
     * @param courseId 课程id
     * @param pic      图片id
     * @return
     */

    @Transactional
    public AddCourseResult addCoursePic(String courseId, String pic) {
        //参数校验
        if (StringUtils.isEmpty(courseId) || StringUtils.isEmpty(pic)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }


        //查询课程中 是否有图片信息
        CoursePic coursePic = this.findCoursePicList(courseId);
        if (coursePic != null) {
            //如果已存在 则修改课程的图片信息
            coursePic.setPic(pic);
        } else {
            //如果不存在 则添加该对应关系
            coursePic = new CoursePic();
            coursePic.setCourseid(courseId);
            coursePic.setPic(pic);
        }
        //保存
        coursePicRepository.save(coursePic);
        return new AddCourseResult(CommonCode.SUCCESS, courseId);
    }

    /**
     * 查询课程图片信息
     *
     * @param courseId 课程id
     * @return
     */
    public CoursePic findCoursePicList(String courseId) {
        //参数校验
        if (StringUtils.isEmpty(courseId)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        CoursePic coursePic = null;
        //查询课程图片信息
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        if (optional.isPresent()) {
            coursePic = optional.get();
        }
        return coursePic;
    }

    /**
     * 课程信息删除
     *
     * @param courseId 课程id
     * @return
     */
    @Transactional
    public DeleteCourseResult deleteCoursePic(String courseId) {
        //校验参数 并查询课程是否有图片
        CoursePic coursePic = this.findCoursePicList(courseId);
        if (coursePic == null) {
            //课程没有找到对应的图片信息
            return new DeleteCourseResult(CourseCode.COURSE_PIC_DELETE_ISNULL, courseId);
        } else {
            //如果存在 则根据课程id 删除该图片信息
            long count = coursePicRepository.deleteByCourseid(courseId);
            if (count < 1) {
                //删除失败
                log.error("delete error By CourseId : {}", courseId);
                return new DeleteCourseResult(CourseCode.COURSE_PIC_DELETE, courseId);
            } else {
                //删除成功
                return new DeleteCourseResult(CommonCode.SUCCESS, courseId);
            }
        }

    }

    /**
     * 课程视图信息查询
     *
     * @param courseId 课程id
     * @return
     */
    public CourseView findCourseView(String courseId) {
        CourseView courseView = new CourseView();
        //查询课程基础信息
        CourseBase courseBase = this.findCourseBaseById(courseId);
        if (courseBase != null) {
            courseView.setCourseBase(courseBase);
        }
        //查询课程图片信息
        CoursePic coursePic = this.findCoursePicList(courseId);
        if (coursePic != null) {
            courseView.setCoursePic(coursePic);
        }
        //查询课程营销信息
        CourseMarket courseMarket = this.findCourseMarketById(courseId);
        if (courseMarket != null) {
            courseView.setCourseMarket(courseMarket);
        }
        //查询教学计划
        TeachplanNode teachPlanList = this.findTeachPlanList(courseId);
        if (teachPlanList != null) {
            courseView.setTeachplanNode(teachPlanList);
        }

        return courseView;
    }

    /**
     * 课程预览  保存课程详细页面
     *
     * @param courseId
     * @return
     */
    public CoursePreviewResult preview(String courseId) {
        //参数校验
        if (StringUtils.isEmpty(courseId)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //查询课程基本信息
        CourseBase courseBase = this.findCourseBaseById(courseId);
        if (courseBase == null) {
            ExceptionCast.cast(CourseCode.COURSE_ISNULL);
        }

        //读取配置文件 设置的课程详细页面的信息
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(coursePublish.getSiteId());//站点id
        cmsPage.setTemplateId(coursePublish.getTemplateId());//模板id
        cmsPage.setPageAliase(courseBase.getName());//设置别名
        cmsPage.setDataUrl(coursePublish.getDataUrlPre() + courseId);//拼接并设置数据url
        cmsPage.setPageWebPath(coursePublish.getPageWebPath());//页面的webPath
        cmsPage.setPagePhysicalPath(coursePublish.getPagePhysicalPath());//设置页面物理路径
        cmsPage.setPageName(courseId + ".html");//页面名称
        //保存课程详细页面
        CmsPageResult cmsPageResult = cmsPageClient.save(cmsPage);
        if (!cmsPageResult.isSuccess()) {
            //保存失败
            return new CoursePreviewResult(CommonCode.FAIL, null);
        }

        //获取保存后的pageId
        String pageId = cmsPageResult.getCmsPage().getPageId();
        //拼接课程预览url
        String url = coursePublish.getPreviewUrl() + pageId;

        return new CoursePreviewResult(CommonCode.SUCCESS, url);
    }


    /**
     * 修改课程发布状态
     *
     * @param courseId
     * @return
     */
    @Transactional
    public CoursePublishResult publish(String courseId) {
        //参数校验
        if (StringUtils.isEmpty(courseId)) {
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        //获取课程基本信息
        CourseBase courseBase = this.findCourseBaseById(courseId);
        //查询查询课程发布状态
        String status = courseBase.getStatus();

        //更新新发布:
        // 保存课程详情页面
        CoursePreviewResult previewResult = this.preview(courseId);
        if (!previewResult.isSuccess()) {
            //保存失败
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_VIEWERROR);
        }
        //保存课程详情页面成功:
        //获取课程详情页面的url
        String url = previewResult.getUrl();
        //截取url后的课程id
        String pageId = StringUtils.substringAfterLast(url, "/");
        //远程调用cms服务 发布页面
        ResponseResult responseResult = cmsPageClient.postPage(pageId);
        if (!responseResult.isSuccess()) {
            //发布失败
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_VIEWERROR);
        }
        //发布成功:

        /*创建课程索引信息*/
        CoursePub coursePub = this.createCoursePub(courseId);
        /*在数据库中记录发布信息 用于添加课程索引*/
        this.saveCoursePub(courseId, coursePub);
        /*保存课程计划媒资信息用于ES*/
        this.saveTeachplanMediaPub(courseId);
        //修改课程发布状态
        courseBase.setStatus("202002");
        courseBaseRepository.save(courseBase);


        // TODO 课程缓存...

        return new CoursePublishResult(CommonCode.SUCCESS, courseBase.getStatus());

    }

    /**
     * 保存课程索引
     *
     * @param courseId
     * @param coursePub
     * @return
     */
    private void saveCoursePub(String courseId, CoursePub coursePub) {

        CoursePub coursePubNew = null;
        //查询数据库中是否已保存
        Optional<CoursePub> optionalPub = coursePubRepository.findById(courseId);
        if (optionalPub.isPresent()) {
            coursePubNew = optionalPub.get();
        } else {
            coursePubNew = new CoursePub();
        }
        //更新数据
        BeanUtils.copyProperties(coursePub, coursePubNew);
        coursePubNew.setId(courseId);
        coursePubNew.setTimestamp(new Date());//时间戳 logStash使用
        coursePubNew.setPubTime(DateUtils.datetoString(new Date(), "yyyy-MM-dd HH:mm:ss"));//发布时间
        coursePubRepository.save(coursePubNew);
    }

    /**
     * 创建课程索引 用于logStash
     *
     * @param courseId 课程id
     */
    private CoursePub createCoursePub(String courseId) {

        //查询课程信息
        CourseView courseView = this.findCourseView(courseId);
        CourseBase courseBase = courseView.getCourseBase();
        CoursePic coursePic = courseView.getCoursePic();
        CourseMarket courseMarket = courseView.getCourseMarket();
        TeachplanNode teachplanNode = courseView.getTeachplanNode();
        //在数据库中记录发布信息
        CoursePub coursePub = new CoursePub();
        //基础信息
        BeanUtils.copyProperties(courseBase, coursePub);
        //课程图片
        BeanUtils.copyProperties(coursePic, coursePub);
        //课程营销信息
        BeanUtils.copyProperties(courseMarket, coursePub);
        BeanUtils.copyProperties(courseBase, coursePub);
        //拼接课程计划
        String teachplan = JSON.toJSONString(teachplanNode);
        coursePub.setTeachplan(teachplan);//课程计划


        return coursePub;

/*      基础信息
        coursePub.setId(courseId);//使用课程id作为主键
        coursePub.setName(courseBase.getName());//课程名称
        coursePub.setUsers(courseBase.getUsers());//适用人群
        coursePub.setMt(courseBase.getMt());//大分类
        coursePub.setSt(courseBase.getSt());//小分类
        coursePub.setGrade(courseBase.getGrade());//课程等级
        coursePub.setStudymodel(courseBase.getStudymodel());//学习模式
        coursePub.setTeachmode(courseBase.getTeachmode());//教育模式
        coursePub.setDescription(courseBase.getDescription());//课程介绍*/

      /* 课程营销信息
        coursePub.setCharge(courseMarket.getCharge());//收费规则，对应数据字典
        coursePub.setValid(courseMarket.getValid());//有效性，对应数据字典
        coursePub.setQq(courseMarket.getQq());//qq
        coursePub.setPrice(courseMarket.getPrice());//价格
        coursePub.setPrice_old(courseMarket.getPrice_old());//原价*/

       /* 课程图片
       coursePub.setPic(coursePic.getPic());*/

    }


    /**拼接Teachplan
     * @param teachplanNode
     * @return
     */
    private String getTeachplan(TeachplanNode teachplanNode) {
        StringBuilder sb = new StringBuilder();
        sb.append(teachplanNode.getPname() + ": ");//拼接主章节名称
        List<TeachplanNode> children = teachplanNode.getChildren();
        for (TeachplanNode child : children) {
            sb.append("\r\n").append("\t").append(child.getPname()).append(": ");   //拼接大章节名称
            for (TeachplanNode child2 : child.getChildren()) {
                sb.append("\r\n").append("\t").append("\t").append(child2.getPname()).append(";");   //拼接小章节名称
            }
        }
        return sb.toString();
    }

    /**
     * 保存课程计划与媒资文件的关联信息
     *
     * @param teachplanMedia
     * @return
     */
    @Transactional
    public ResponseResult saveMedia(TeachplanMedia teachplanMedia) {
        String courseId = teachplanMedia.getCourseId();//课程id
        String mediaId = teachplanMedia.getMediaId();//媒资文件id
        String mediaFileOriginalName = teachplanMedia.getMediaFileOriginalName();//文件名称
        String mediaUrl = teachplanMedia.getMediaUrl();//文件访问相对路径
        String teachplanId = teachplanMedia.getTeachplanId();//课程计划id

        if (StringUtils.isEmpty(courseId) || StringUtils.isEmpty(mediaId)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        if (StringUtils.isEmpty(mediaFileOriginalName)) {
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_NAMEISNULL);
        }
        if (StringUtils.isEmpty(mediaUrl)) {
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_URLISNULL);
        }
        Optional<Teachplan> optional = teachplanRepository.findById(teachplanId);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_ISNULL);
        }
        Teachplan teachplan = optional.get();
        String grade = teachplan.getGrade();
        if (StringUtils.isEmpty(grade) || !grade.equals("3")) {
            //只允许第3级节点添加媒资文件信息
            ExceptionCast.cast(MediaCode.COURSE_MEDIA_TEACHPLAN_GRADEERROR);
        }
        //查询数据库中是否存在关联信息
        Optional<TeachplanMedia> teachplanMediaOptional = teachplanMediaRepository.findById(teachplanId);
        TeachplanMedia teachplanMediaNew = null;
        if (teachplanMediaOptional.isPresent()) {
            teachplanMediaNew = teachplanMediaOptional.get();
        } else {
            teachplanMediaNew = new TeachplanMedia();
        }
        teachplanMediaNew.setCourseId(courseId);
        teachplanMediaNew.setMediaFileOriginalName(mediaFileOriginalName);
        teachplanMediaNew.setMediaId(mediaId);
        teachplanMediaNew.setMediaUrl(mediaUrl);
        teachplanMediaNew.setTeachplanId(teachplanId);
        TeachplanMedia save = teachplanMediaRepository.save(teachplanMediaNew);
        return new ResponseResult(CommonCode.SUCCESS);
    }


    /** 保存课程计划媒资信息用于ES  先删后增
     * @param courseId
     */
    private void saveTeachplanMediaPub(String courseId) {

        if (StringUtils.isEmpty(courseId)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        /*查询课程计划中所有媒资信息*/
        List<TeachplanMedia> teachplanMediaList = teachplanMediaRepository.findByCourseId(courseId);
        /*删除原来的课程计划中所有媒资信息*/
        teachplanMediaPubRepository.deleteByCourseId(courseId);

        //将所有信息遍历保存
        List<TeachplanMediaPub> teachplanMediaPubList = new ArrayList<>();
        for (TeachplanMedia teachplanMedia : teachplanMediaList) {
            TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
            BeanUtils.copyProperties(teachplanMedia, teachplanMediaPub);
            teachplanMediaPub.setTimestamp(new Date());
            teachplanMediaPubList.add(teachplanMediaPub);
        }
        teachplanMediaPubRepository.saveAll(teachplanMediaPubList);
    }
}
