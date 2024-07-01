package com.self_back.Handler;

import com.self_back.mapper.FileInfoMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;
import java.util.List;

@Component
public class FileCleanHandler {
    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Scheduled(fixedRate = 1000 * 60 * 5)
    public void cleanFiles() {
        // 查询所有需要删除的path
        List<String> pathsToDelete = fileInfoMapper.findPathsToDelete();

        for (String path : pathsToDelete) {
            File file = new File(path);
            if (file.exists()) {
                file.delete(); // 删除文件
            }

            // 检查并删除附带文件或文件夹
            if (path.endsWith(".mp4")) {
                deleteRelatedFiles(path, ""); // 删除同名文件夹
            }
            if (path.endsWith(".pptx")) {
                deleteRelatedFiles(path, ".pdf"); // 删除对应的PDF文件
            }
            if (path.endsWith(".jpg") || path.endsWith(".png")) {
                deleteRelatedFiles(path, "_sf.png"); // 删除特定后缀的图片文件
            }

            fileInfoMapper.deleteRecordsByPath(path); // 删除数据库记录
        }

        // 删除没有path的记录
        fileInfoMapper.deleteRecordsWithoutPath();
    }

    private void deleteRelatedFiles(String path, String suffix) {
        String baseName = path.substring(0, path.lastIndexOf('.'));
        File relatedFile = new File(baseName + suffix);
        if (relatedFile.exists()) {
            if (relatedFile.isDirectory()) {
                deleteDirectory(relatedFile);
            } else {
                relatedFile.delete();
            }
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File f : files) {
                deleteDirectory(f);
            }
        }
        directory.delete();
    }
    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    public void cleanRecycleBin() {
        fileInfoMapper.updateDeletedRecords();
    }
}
