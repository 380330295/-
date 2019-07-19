package com.xuecheng.manage_media_process.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Kiku
 * @date 2019/7/12 9:52
 */
@Service
@Slf4j
public class MediaFileService {
    @Autowired
    private MediaFileRepository mediaFileRepository;
    @Value("${xc-service-manage-media.video-location}")
    private String videoLocation;//上传文件根目录
    @Value("${xc-service-manage-media.ffmpeg-path}")
    private String ffmpegPath;//ffmpeg绝对路径


    /**
     * 查询媒资文件信息
     *
     * @param mediaId
     */
    public MediaFile queryMediaFileById(String mediaId) {
        if (StringUtils.isEmpty(mediaId)) {
            log.error("invalid param : mediaId is null ->{}", mediaId);
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        if (!optional.isPresent()) {
            ExceptionCast.cast(MediaCode.MEDIA_FILE_NOT_EXIST);
        }
        return optional.get();
    }

    /**
     * 保存媒资文件信息
     *
     * @param mediaFile
     */
    public void saveMediaFile(MediaFile mediaFile) {
        if (mediaFile == null) {
            log.error("invalid param : MediaFile is null ->{}", mediaFile);
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        mediaFileRepository.save(mediaFile);
    }

    /**
     * 生成mp4文件
     *
     * @param mediaFile
     */
    public boolean fileTypeToMp4(MediaFile mediaFile) {
        String filePath = mediaFile.getFilePath();//文件相对路径
        String fileName = mediaFile.getFileName();//文件名
        //转换文件(avi)文件所在路径 = 根目录+文件相对路径+文件名
        String videoPath = videoLocation + filePath + fileName;
        String mp4FileName = mediaFile.getFileId() + ".mp4"; //mp4文件名称
        String mp4FolderPath = videoLocation + filePath; //mp4文件保存的目录
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpegPath, videoPath, mp4FileName, mp4FolderPath);
        //开始生成mp4 得到结果
        String result = mp4VideoUtil.generateMp4();
        if (result == null || !result.equals("success")) {
            //生成失败  记录处理日志
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            //修改处理状态为处理失败
            mediaFile.setProcessStatus("303003");
            //设置错误信息
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            /*保存信息*/
            this.mediaFileRepository.save(mediaFile);
            return false;
        }
        return true;
    }


    /**
     * 生成M3u8文件
     *
     * @param mediaFile
     * @return
     */
    public boolean fileTypeToM3u8(MediaFile mediaFile) {
        String filePath = mediaFile.getFilePath();//文件相对路径
        //mp4文件所在路径 =  根目录+文件相对路径+文件名
        String mp4FilePath = videoLocation + filePath + mediaFile.getFileId() + ".mp4";
        String m3u8Name = mediaFile.getFileId() + ".m3u8";//m3u8文件名称
        String m3u8FolderPath = videoLocation + filePath + "hls/"; //m3u8文件保存的目录 = 根目录+文件相对路径+/hls/
        HlsVideoUtil hlsVideoUtil = new HlsVideoUtil(ffmpegPath, mp4FilePath, m3u8Name, m3u8FolderPath);
        MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
        String result = hlsVideoUtil.generateM3u8();
        if (result == null || !result.equals("success")) {
            //生成失败  记录处理日志
            //设置错误信息
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            //修改处理状态为处理失败
            mediaFile.setProcessStatus("303003");
            /*保存信息*/
            this.mediaFileRepository.save(mediaFile);
            return false;
        }
        //生成成功获取m3u8 ts文件列表
        List<String> ts_list = hlsVideoUtil.get_ts_list();
        //保存文件信息
        mediaFileProcess_m3u8.setTslist(ts_list);
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
        //修改处理状态为处理成功
        mediaFile.setProcessStatus("303002");
        /*保存信息*/
        //生成m3u8成功后更改文件url为 m3u8的Url
        mediaFile.setFileUrl(filePath + "hls/" + m3u8Name);
        this.mediaFileRepository.save(mediaFile);
        return true;
    }
}