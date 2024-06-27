package com.self_back.Handler;
import com.self_back.Exception.BusinessException;
import com.self_back.mapper.FileInfoMapper;
import com.self_back.mapper.UserMapper;
import com.self_back.utils.*;
import jdk.internal.org.jline.terminal.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import com.self_back.entity.po.FileInfo;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@Component
public class RabbitMqHandler {
    @Resource
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    private FileInfoMapper fileInfoMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @RabbitListener(queues = "upload_file_queue")
    @Transactional
    public void uploadFile(Map<String,Object> msgMap) {
        log.info("收到文件上传消息");
        log.info(msgMap.toString());
        int totalChunks = (int) msgMap.get("totalChunks");
        String identifier = (String) msgMap.get("identifier");
        String filename = (String) msgMap.get("filename");
        long totalSize = (long) msgMap.get("totalSize");
        int userId = (int) msgMap.get("userId");
        //目标文件路径
        File fileFinal = new File(Constant.FILE_PATH + userId + "/" + filename);
        File dis = new File(Constant.FILE_PATH + userId);
        if (!dis.exists()) {
            dis.mkdirs();
        }
        String filePath = fileFinal.getPath();
        taskExecutor.execute(() -> {
            try {
                mergeFileParts(filePath, identifier, totalChunks);
                userMapper.updateUseSpace(userId, totalSize);
                String userInfoKey = "userInfo:" + userId;
                Map<String,String> updateInfo = new HashMap<>();
                updateInfo.put("update_userInfo",userInfoKey);
                rabbitTemplate.convertAndSend("selfpan","sql_and_redis_key",updateInfo);
                deleteFileParts(identifier, totalChunks);
                if(filename.endsWith(".ppt") || filename.endsWith(".pptx")) {
                    String tardir = filePath.substring(0,filePath.lastIndexOf("\\"));
                    PdfConvertUtil.convert2Pdf(filePath, tardir);
                }

            } catch (Exception e) {
                throw new RuntimeException("文件处理有问题：", e);
            }
        });

        //将文件信息保存到数据库
        int type = FileCategorizer.getFileType(filename);
        int category = FileCategorizer.getFileCategory(filename);

        FileInfo fileInfo = new FileInfo();
        fileInfo.setUserId(userId);
        fileInfo.setName(filename);
        fileInfo.setPath(filePath);
        fileInfo.setSize(totalSize);
        fileInfo.setType(type);
        fileInfo.setCategory(category);
        fileInfo.setPid(msgMap.get("pid") == null ? 0 : (int) msgMap.get("pid"));
        fileInfo.setCreateTime(new Date());
        fileInfo.setLastUpdateTime(new Date());
        fileInfo.setMd5((String) msgMap.get("identifier"));
        //处理封面图片 && 处理视频分片实施展示给前端
        if(type == 1 || type == 8) {
            String coverPath = filePath.substring(0, filePath.lastIndexOf(".")) + "_sf" + Constant.IMG_END;
            //处理视频
            if (type == 1) {
                cutFile4Video(identifier, filePath);
                ScaleFilter.createCover4Video(new File(filePath), 150, new File(coverPath));
            }
            //处理图片
            else {
                ScaleFilter.compressImage(new File(filePath), 150, new File(coverPath), false);
            }
            fileInfo.setCover(coverPath);
        }
        fileInfoMapper.addFile(fileInfo);
    }
    @RabbitListener(queues = "sql_and_redis_queue")
    public void CacheHandeler(Map msg) {
        log.info("收到缓存更新消息");
        if (msg.get("update_userInfo") != null) {
            String userInfoKey = (String) msg.get("update_userInfo");
            redisTemplate.delete(userInfoKey);
        }
    }
    /**
     * 合并所有的切片到最终的文件
     * @param filePath 目标文件路径
     * @param identifier 文件标识符
     * @param totalChunks 总切片数
     */
    private void mergeFileParts(String filePath, String identifier, int totalChunks) throws IOException {
        try (BufferedOutputStream mergeStream = new BufferedOutputStream(new FileOutputStream(filePath, true))) {
            for (int i = 1; i <= totalChunks; i++) {
                File partFile = new File(Constant.FILE_PATH + "/temp/" +  identifier + "_" + i);
                try (BufferedInputStream partStream = new BufferedInputStream(new FileInputStream(partFile))) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = partStream.read(buffer)) != -1) {
                        mergeStream.write(buffer, 0, bytesRead);
                    }
                }
                partFile.delete();
            }
        }
    }

    /**
     * 删除所有的切片
     * @param identifier 文件标识符
     * @param totalChunks 总切片数
     */
    private void deleteFileParts(String identifier, int totalChunks) {
        for (int i = 1; i <= totalChunks; i++) {
            File partFile = new File(Constant.FILE_PATH + "/temp/" + identifier + "_" + i);
            if (partFile.exists()) {
                partFile.delete();
            }
        }
    }
    private void cutFile4Video(String md5, String videoFilePath) {
        // 创建同名切片目录
        File tsFolder = new File(videoFilePath.substring(0, videoFilePath.lastIndexOf(".")));
        if (!tsFolder.exists()) {
            tsFolder.mkdirs();
        }
        // 定义生成 HLS 的命令
        final String CMD_2M3U8_2TS = "ffmpeg -y -i %s -c:v copy -c:a aac -bsf:a aac_adtstoasc -hls_time 30 -hls_list_size 0 -hls_segment_filename %s_%%04d.ts %s";

        // 设置 M3U8 文件的路径
        String m3u8Path = tsFolder + "/index.m3u8";
        // TS 文件名模式
        String tsSegmentPattern = tsFolder.getPath() + "/" + md5;


        // 生成.m3u8 和.ts 片段
        String cmd = String.format(CMD_2M3U8_2TS, videoFilePath, tsSegmentPattern, m3u8Path);
        ProcessUtils.executeCommand(cmd, false);
    }
}
