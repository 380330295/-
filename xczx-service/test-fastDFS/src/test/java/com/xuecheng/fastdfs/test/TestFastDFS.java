package com.xuecheng.fastdfs.test;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.proto.storage.DownloadFileWriter;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.github.tobato.fastdfs.service.TrackerClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Kiku
 * @date 2019/7/5 12:51
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {

    @Autowired
    private FastFileStorageClient storageClient;

    //上传文件
    @Test
    public void testUpload() {

        FileInputStream fis = null;
        try {
            //获取流
             fis = new FileInputStream("h:/7.jpg");
             //指定流  文件大小 文件拓展名
            StorePath path = storageClient.uploadFile(fis, 103845L, "jpg", null);
            System.out.println(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fis!=null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

        //M00/00/00/wKiJgF0NEneAYkS0AAGVpY2PJls392.jpg
    //下载文件
    @Test
    public void testDownload(){

        //从storage下载文件
        byte[] b = storageClient.downloadFile("group1",
                        "M00/00/00/wKiJgF0NF72ANWXKAAGVpY2PJls547.jpg",new DownloadByteArray());
        FileOutputStream fos = null;
        try {
            //保存到本地
            fos = new FileOutputStream("d:/test.jpg");
            fos.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
