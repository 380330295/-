package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * @author Kiku
 * @date 2019/7/10 16:46
 */
@Service
public class MediaUploadService {

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Value("${xc-service-manage-media.upload-location}")
    private String uploadPath;
    @Value("${xc-service-manage-media.mq.routingkey‐media‐video}")
    private String routingKey;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /** 文件上传前准备工作
     * @param fileMd5
     * @param fileName
     * @param fileSize
     * @param mimeType
     * @param fileExt
     * @return
     */
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimeType, String fileExt) {

        /*查询文件是否上传 1.查本地  2.查数据库 同时存在则存在 */

        /*得到文件路径*/
        String filePath = this.getFilePath(fileMd5, fileExt);
        File file = new File(filePath);
        //查看本地是否存在该文件
        boolean exists = file.exists();
        //查询数据库是否存在
        Optional<MediaFile> optionalFile = mediaFileRepository.findById(fileMd5);
        if (exists && optionalFile.isPresent()) {
            //存在该文件 返回前端已上传
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }
        //不存在
        /*创建文件存储目录*/
        boolean bool = this.createFileFold(fileMd5);
        if (!bool) {
            //上传文件目录创建失败
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_CREATE_FOLDER_FAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }


    /** 校验快文件
     * @param fileMd5
     * @param chunk
     * @param chunkSize
     * @return
     */
    public CheckChunkResult checkChunk(String fileMd5, Integer chunk, Long chunkSize) {
        /*获取快文件目录*/
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        String chunkFilePath = chunkFileFolderPath + chunk;
        File file = new File(chunkFilePath);
        if (file.exists()) {
            return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK, true);
        } else {
            return new CheckChunkResult(MediaCode.CHUNK_FILE_NOT_EXIST_CHECK, false);
        }
    }

    /** 分块文件上传
     * @param file
     * @param chunk
     * @param fileMd5
     * @return
     */
    public ResponseResult uploadChunk(MultipartFile file, Integer chunk, String fileMd5) {
        /*获取块文件所在目录*/
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        //块文件目录
        File chunkFile = new File(chunkFileFolderPath);
        if (!chunkFile.exists()) {
            //如果不存在 创建该目录
            chunkFile.mkdirs();
        }
        /*存储块文件*/
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            //获取上传文件的输入流
            inputStream = file.getInputStream();
            //创建文件输出流 指定输出路径
            outputStream = new FileOutputStream(chunkFileFolderPath + chunk);
            //工具类上传
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /** 分块合并 保存文件
     * @param fileMd5
     * @param fileName
     * @param fileSize
     * @param mimeType
     * @param fileExt
     * @return
     */
    public ResponseResult mergeChunks(String fileMd5, String fileName,
                                      Long fileSize, String mimeType, String fileExt) {
        /*获取块文件目录*/
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        File chunkFileFolder = new File(chunkFileFolderPath);
        /*获取块文件列表*/
        List<File> fileList = this.getChunkFiles(chunkFileFolder);
        /*获取写入的文件路径*/
        String filePath = this.getFilePath(fileMd5, fileExt);
        File file = new File(filePath);
        /*合并块文件*/
        file = this.mergeFile(file, fileList);
        /*校验文件md5值*/
        boolean b = this.checkFileMd5(file, fileMd5);
        if (!b){
            ExceptionCast.cast(MediaCode.MERGE_FILE_CHECK_FAIL);
        }
        //往mongodb保存文件信息
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId(fileMd5); //文件id
        mediaFile.setFileName(fileMd5 + "." + fileExt); //文件名称
        mediaFile.setFileSize(fileSize);//文件大小
        mediaFile.setFileOriginalName(fileName);//文件原始名称
        mediaFile.setFilePath(this.getFileFolderRelativePath(fileMd5));//文件目录
        mediaFile.setMimeType(mimeType);//文件mime类型
        mediaFile.setFileType(fileExt);//文件类型
        mediaFile.setFileUrl(this.getFileFolderRelativePath(fileMd5)+ fileMd5 + "." + fileExt);//文件url
        mediaFile.setUploadTime(new Date());//上传时间
        mediaFile.setFileStatus("301002");//状态为上传成功
        /*保存*/
        MediaFile save = mediaFileRepository.save(mediaFile);
        /*向MQ发送视频处理消息*/
        sendProcessVideoMsg(fileMd5);
        return new ResponseResult(CommonCode.SUCCESS);
    }
    /*向MQ发送视频处理消息*/
    private void sendProcessVideoMsg(String mediaId) {
        if (StringUtils.isEmpty(mediaId)){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        if (!optional.isPresent()){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Map<String,String> msgMap = new HashMap<>();
        msgMap.put("mediaId",mediaId);
        String msg = JSON.toJSONString(msgMap);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EX_MEDIA_PROCESSTASK,routingKey,msg);

    }


    /*获取块排序后文件列表*/
    private List<File> getChunkFiles(File chunkFileFolder) {
        File[] files = chunkFileFolder.listFiles();
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName()) > Integer.parseInt(o2.getName())) {
                    return 1;
                }
                return -1;
            }
        });
        return fileList;
    }

    /*校验文件md5值*/
    private boolean checkFileMd5(File file, String fileMd5) {
        if (file == null || StringUtils.isEmpty(fileMd5)) {
            return false;
        }
        //进行md5校验
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            //得到文件的md5
            String md5Hex = DigestUtils.md5Hex(inputStream);
            //比较
            if (fileMd5.equalsIgnoreCase(md5Hex)) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /*合并块文件*/
    private File mergeFile(File file, List<File> fileList) {
        try {
            //创建写文件对象
            RandomAccessFile raf_write = new RandomAccessFile(file, "rw");
            //读取文件缓存区
            byte[] bytes = new byte[1024 * 8];
            //遍历块文件列表
            for (File chunkFile : fileList) {
                //分块文件对象
                RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "r");
                int len;
                //读取分块文件合并文件
                while ((len = raf_read.read(bytes)) != -1) {
                    raf_write.write(bytes, 0, len);
                }
                raf_read.close();
            }
            raf_write.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }
    /* 创建上传文件文件目录 */
    private boolean createFileFold(String fileMd5) {
        /*获得文件上传的目录*/
        String fileFolderPath = this.getFileFolderPath(fileMd5);
        File file = new File(fileFolderPath);
        //查询目录是否存在
        if (!file.exists()) {
            //如果不存在 则创建多级目录
            return file.mkdirs();
        }
        //如果已存在 则不创建
        return true;
    }

    /*文件相对路径*/
    private String getFileFolderRelativePath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
    }

    /*文件路径 = 文件上传目录 + 文件名*/
    private String getFilePath(String fileMd5, String fileExt) {
        return this.getFileFolderPath(fileMd5) + fileMd5 + "." + fileExt;
    }

    /*块文件所在目录 = 文件上传目录 + /chunk/ */
    private String getChunkFileFolderPath(String fileMd5) {
        return this.getFileFolderPath(fileMd5) + "chunk/";
    }

    /*文件上传的目录 = 一级目录：md5的第一个字符 + 二级目录：md5的第二个字符 + 三级目录：md5*/
    private String getFileFolderPath(String fileMd5) {
        return uploadPath + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
    }

}
