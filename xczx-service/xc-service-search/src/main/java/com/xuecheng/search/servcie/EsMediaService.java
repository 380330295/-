package com.xuecheng.search.servcie;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.search.config.CourseSearchProperties;
import com.xuecheng.search.config.MediaSearchProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.highlight.QueryScorer;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Kiku
 * @date 2019/7/14 18:16
 */
@Service
@EnableConfigurationProperties(MediaSearchProperties.class)
@Slf4j
public class EsMediaService {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Autowired
    MediaSearchProperties searchProperties;

    /**
     * 根据所有teachplanId获取媒资信息
     *
     * @param teachplanIds
     * @return
     */
    public QueryResponseResult<TeachplanMediaPub> getmedia(String[] teachplanIds) {

        if (teachplanIds.length < 1) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //设置查询的索引
        SearchRequest searchRequest = new SearchRequest(searchProperties.getIndex());
        //设置类型
        searchRequest.types(searchProperties.getType());
        //搜索源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //精确查询
        searchSourceBuilder.query(QueryBuilders.termsQuery("teachplan_id", teachplanIds));

        //字段过滤
        searchSourceBuilder.fetchSource(searchProperties.getSourceField(), new String[]{});
        //请求搜索
        searchRequest.source(searchSourceBuilder);

        List<TeachplanMediaPub> teachplanMediaPubList = new ArrayList<>();
        try {
            //获取查询结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
//            处理结果集
            for (SearchHit searchHit : searchHits) {
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
                //取出课程计划媒资信息
                String courseid = (String) sourceAsMap.get("courseid");
                String media_id = (String) sourceAsMap.get("media_id");
                String media_url = (String) sourceAsMap.get("media_url");
                String teachplan_id = (String) sourceAsMap.get("teachplan_id");
                String media_fileoriginalname = (String) sourceAsMap.get("media_fileoriginalname");
                teachplanMediaPub.setCourseId(courseid);
                teachplanMediaPub.setMediaUrl(media_url);
                teachplanMediaPub.setMediaFileOriginalName(media_fileoriginalname);
                teachplanMediaPub.setMediaId(media_id);
                teachplanMediaPub.setTeachplanId(teachplan_id);
                //添加到数据列表
                teachplanMediaPubList.add(teachplanMediaPub);
            }
        } catch (Exception e) {
            log.error("search TeachplanMediaPub error :{}",e.getMessage());
        }
        QueryResult<TeachplanMediaPub> queryResult = new QueryResult<>();
        queryResult.setList(teachplanMediaPubList);
        queryResult.setTotal(teachplanMediaPubList.size());
        return new QueryResponseResult<TeachplanMediaPub>(CommonCode.SUCCESS,queryResult);
    }
}
