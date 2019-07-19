package com.xuecheng.search.servcie;

import com.xuecheng.api.search.EsCourseControllerApi;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.search.config.CourseSearchProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kiku
 * @date 2019/7/9 16:33
 */
@Service
@EnableConfigurationProperties(CourseSearchProperties.class)
@Slf4j
public class EsCourseService {

    @Autowired
    CourseSearchProperties searchProperties;
    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * 课程综合搜索
     *
     * @param page
     * @param size
     * @param courseSearchParam
     * @return
     */
    public QueryResponseResult<CoursePub> queryList(int page, int size, CourseSearchParam courseSearchParam) {


        //设置搜索索引
        SearchRequest searchRequest = new SearchRequest(searchProperties.getIndex());
        //设置类型
        searchRequest.types(searchProperties.getType());
        //搜索源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //布尔查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();


        //source源字段过滤
        String[] sourceField = searchProperties.getSourceField();
        searchSourceBuilder.fetchSource(sourceField, new String[]{});

        //关键字高亮查询
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font class='esLight'>");
        highlightBuilder.postTags("</font>");
        //设置关键字高亮
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);

        /*按关键字搜索*/
        if (StringUtils.isNotEmpty(courseSearchParam.getKeyword())) {
            //匹配关键字
            MultiMatchQueryBuilder multiMatchQueryBuilder =
                    QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "name", "teachplan", "description");
            //设置匹配占比
            multiMatchQueryBuilder.minimumShouldMatch("70%");
            //提高name字段的Boost值
            multiMatchQueryBuilder.field("name", 10);
            //添加到布尔查询
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        /*按一级分类查询*/
        if (StringUtils.isNotEmpty(courseSearchParam.getMt())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("mt", courseSearchParam.getMt()));
        }
        /*按二级分类查询*/
        if (StringUtils.isNotEmpty(courseSearchParam.getSt())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("st", courseSearchParam.getSt()));
        }
        /*按难度等级查询*/
        if (StringUtils.isNotEmpty(courseSearchParam.getGrade())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("grade", courseSearchParam.getGrade()));
        }


        //设置查询方式为布尔查询
        searchSourceBuilder.query(boolQueryBuilder);

        /*分页查询*/
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 8;
        }
        //起始记录下标
        int from = (page - 1) * size;
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);

        //请求搜索
        searchRequest.source(searchSourceBuilder);

        //响应结果对象
        SearchResponse searchResponse = null;
        try {
            //获取查询结果
            searchResponse = restHighLevelClient.search(searchRequest);
        } catch (IOException e) {
            log.error("xuecheng search error..{}", e.getMessage());
            return new QueryResponseResult<CoursePub>(CommonCode.SUCCESS, new QueryResult<CoursePub>());

        }
        //结果集处理
        SearchHits hits = searchResponse.getHits();
        ////记录总数
        long totalHits = hits.getTotalHits();
        SearchHit[] searchHits = hits.getHits();

        //创建数据列表
        List<CoursePub> list = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            CoursePub coursePub = new CoursePub();

            //取出source
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //取出名称
            String name = (String) sourceAsMap.get("name");
            //取出高亮字段name
            Map<String, HighlightField> fields = hit.getHighlightFields();
            if (fields != null) {
                HighlightField highlightFieldName = fields.get("name");
                if (highlightFieldName != null) {
                    //获取所有片段
                    Text[] fragments = highlightFieldName.fragments();
                    StringBuilder sb = new StringBuilder();
                    //拼接片段信息
                    for (Text fragment : fragments) {
                        sb.append(fragment);
                    }
                    name = sb.toString();
                }
            }
            coursePub.setName(name);
            //取出图片
            String pic = (String) sourceAsMap.get("pic");
            coursePub.setPic(pic);
            //取出价格
            Double price = (Double) sourceAsMap.get("price");
            coursePub.setPrice(price);
            //取出原价
            Double price_old = (Double) sourceAsMap.get("price_old");
            coursePub.setPrice_old(price_old);
            //取出id
            String id = (String) sourceAsMap.get("id");
            coursePub.setId(id);
            //添加进数据列表
            list.add(coursePub);
        }

        //设置查询结果对象
        QueryResult<CoursePub> queryResult = new QueryResult<>(list, totalHits);

        return new QueryResponseResult<>(CommonCode.SUCCESS, queryResult);
    }

    /**
     * 根据课程id的集合获取课程信息
     *
     * @param id
     * @return
     */
    public Map<String, CoursePub> getCourseByIds(String id) {
        if (StringUtils.isEmpty(id)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //设置搜索索引
        SearchRequest searchRequest = new SearchRequest(searchProperties.getIndex());
        //设置类型
        searchRequest.types(searchProperties.getType());
        //搜索源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        /*按id搜索查询*/
        searchSourceBuilder.query(QueryBuilders.termQuery("id", id));
        searchRequest.source(searchSourceBuilder);

        Map<String, CoursePub> map = new HashMap<>();
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] hitsHits = hits.getHits();
            for (SearchHit hitsHit : hitsHits) {
                Map<String, Object> sourceAsMap = hitsHit.getSourceAsMap();
                String courseId = (String) sourceAsMap.get("id");
                String name = (String) sourceAsMap.get("name");
                String grade = (String) sourceAsMap.get("grade");
                String charge = (String) sourceAsMap.get("charge");
                String pic = (String) sourceAsMap.get("pic");
                String description = (String) sourceAsMap.get("description");
                String teachplan = (String) sourceAsMap.get("teachplan");
                CoursePub coursePub = new CoursePub();
                coursePub.setId(courseId);
                coursePub.setName(name);
                coursePub.setPic(pic);
                coursePub.setGrade(grade);
                coursePub.setTeachplan(teachplan);
                coursePub.setDescription(description);
                coursePub.setCharge(charge);
                map.put(courseId, coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
