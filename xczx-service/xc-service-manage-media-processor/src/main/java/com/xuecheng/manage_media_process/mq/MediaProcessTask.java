package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.manage_media_process.service.MediaFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @author Kiku
 * @date 2019/7/12 9:42
 */
@Component
@Slf4j
public class MediaProcessTask {

    @Autowired
    private MediaFileService mediaFileService;

    //监听队列
    @RabbitListener(queues = {"${xc-service-manage-media.mq.queue-media-video-processor}"},
                    containerFactory = "customContainerFactory")
    public void receiveMediaProcessTask(String msg) throws IOException {
        //解析json
        Map map = JSON.parseObject(msg, Map.class);
        //取出mediaId
        String mediaId = (String) map.get("mediaId");
        /*获取媒资文件信息*/
        MediaFile mediaFile = mediaFileService.queryMediaFileById(mediaId);
        //校验媒资文件类型  此处只实现 avi类型媒资文件转换
        String fileType = mediaFile.getFileType();
        if (fileType == null || !fileType.equals("avi")) {
            mediaFile.setProcessStatus("303004");//修改处理状态为无需处理
            /*保存媒资文件信息*/
            mediaFileService.saveMediaFile(mediaFile);
            return;
        }
        mediaFile.setProcessStatus("303001");//处理状态为未处理
        /*保存媒资文件信息*/
        mediaFileService.saveMediaFile(mediaFile);
        /*生成mp4文件*/
        boolean toMp4Result = mediaFileService.fileTypeToMp4(mediaFile);
        if (!toMp4Result){
            log.error("toMp4Result is false , fileTypeToMp4 error");
            return;
        }
        /*生成m3u8*/
        boolean toM3u8Result =  mediaFileService.fileTypeToM3u8(mediaFile);
        if (!toM3u8Result){
            log.error("toM3u8Result is false , fileTypeToM3u8 error");
        }

    }
}
