package com.self_back.service;

import com.self_back.entity.dto.FileUploadDTO;
import com.self_back.entity.po.FileInfo;
import com.self_back.entity.vo.FilesVO;
import com.self_back.mapper.FileInfoMapper;
import com.self_back.mapper.UserMapper;
import com.self_back.utils.Constant;
import com.self_back.utils.Result;
import com.self_back.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FileInfoService {
    @Autowired
    private FileInfoMapper fileInfoMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private UserMapper userMapper;

    public void addFolder(String token, String folderName, int pid) {
        int userId = TokenUtil.parseToken(token);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(folderName);
        fileInfo.setUserId(userId);
        fileInfo.setPid(pid);
        fileInfo.setCreateTime(new Date());
        fileInfo.setLastUpdateTime(new Date());
        fileInfo.setType(0);
        fileInfoMapper.addFolder(fileInfo);

    }

    //这个是传token的
    public List<FilesVO> getFileInfoByFileIdAndCategory(String token, Integer id, Integer category) {
        int userId = TokenUtil.parseToken(token);
        return fileInfoMapper.getFileInfoByFileIdAndCategory(userId, id, category);
    }
    //这个是传userid的
    public List<FilesVO> getFileInfoByFileIdAndCategory2(Integer userId, Integer id, Integer category) {
        return fileInfoMapper.getFileInfoByFileIdAndCategory(userId, id, category);
    }
    public void changeFileName(int fileId, String newName) {
        fileInfoMapper.updateFileName(fileId, newName, new Date());
    }

    public void deleteFile(int[] ids) {
        for (int id : ids) {
            recursivelyDeleteFile(id);
        }
    }
    @Transactional
    public void recursivelyDeleteFile(int id) {
        fileInfoMapper.markAsDeleted(id);
        FileInfo fileInfo = fileInfoMapper.getFileById(id);
        log.info("删除文件：" + fileInfo);
        if (fileInfo.getType() != 0) {
            int userId = fileInfo.getUserId();
            Long size = fileInfo.getSize();
            userMapper.updateUseSpace(userId, -size);
            String userInfoKey = "userInfo:" + fileInfo.getUserId();
            Map<String,String> updateInfo = new HashMap<>();
            updateInfo.put("update_userInfo",userInfoKey);
            rabbitTemplate.convertAndSend("selfpan","sql_and_redis_key",updateInfo);
        }
        List<Integer> childIds = fileInfoMapper.findChildrenById(id);
        for (Integer childId : childIds) {
            recursivelyDeleteFile(childId);
        }
    }
    public void deleteFile2(int[] ids) {
        for (int id : ids) {
            recursivelyDeleteFile2(id);
        }
    }

    private void recursivelyDeleteFile2(int id) {
        fileInfoMapper.markAsDeleted2(id);
        List<Integer> childIds = fileInfoMapper.findChildrenById2(id);
        for (Integer childId : childIds) {
            recursivelyDeleteFile2(childId);
        }
    }
    public void refresh(int[] ids) {
        for (int id : ids) {
            recursivelyrefresh(id);
        }
    }

    private void recursivelyrefresh(int id) {
        fileInfoMapper.markAsRefresh(id);
        List<Integer> childIds = fileInfoMapper.findChildrenById3(id);
        for (Integer childId : childIds) {
            recursivelyrefresh(childId);
        }
    }
    public void upLoadFile(String token, FileUploadDTO fileUploadDTO) {
        int userId = TokenUtil.parseToken(token);
        int index = fileUploadDTO.getChunkNumber();
        int totalChunks = fileUploadDTO.getTotalChunks();
        String identifier = fileUploadDTO.getIdentifier();
        fileUploadDTO.setUserId(userId);
        String userInfoKey = "userInfo:" + userId;
        Long useSpace = (Long) redisTemplate.opsForHash().get(userInfoKey, "useSpace");
        if(fileUploadDTO.getTotalSize() + useSpace > 10737418240L ) {
            throw new RuntimeException("空间不足");
        }
        // 确保文件夹路径存在
        File dir = new File(Constant.FILE_PATH + "/temp/");
        if (!dir.exists()) {
            dir.mkdirs(); // 创建目录
        }

        File file = new File(dir, identifier + "_" + index);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(fileUploadDTO.getFile().getBytes());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("文件未找到", e);
        } catch (IOException e) {
            throw new RuntimeException("不能写入文件", e);
        }
        //最后一片后，合并文件
        if (index == totalChunks) {
            Map<String, Object> msgMap = new HashMap<>();
            msgMap.put("userId", userId);
            msgMap.put("pid", fileUploadDTO.getPid());
            msgMap.put("totalChunks", totalChunks);
            msgMap.put("identifier", identifier);
            msgMap.put("filename", fileUploadDTO.getFilename());
            msgMap.put("totalSize", fileUploadDTO.getTotalSize());
            rabbitTemplate.convertAndSend("selfpan","upload_file_key", msgMap);
        }
    }

    public List<Map<String, Object>> getPath(int id) {
        return fileInfoMapper.getPath(id);
    }

    public String searchPathById(int id) {
        return fileInfoMapper.getPathById(id);
    }

    public List<FilesVO> getFileInRecycle(String token) {
        int userId = TokenUtil.parseToken(token);
        return fileInfoMapper.getFileInRecycle(userId);
    }

    public List<FilesVO> getAllFoldersById(String token,int fileId) {
        int userId = TokenUtil.parseToken(token);
        return fileInfoMapper.getAllFoldersById(userId,fileId);
    }

    public void changeFolder(int tarId, int[] ids) {
        for (int id : ids) {
            if (isSubFolder(tarId, id)) {
                throw new IllegalArgumentException("不能移动到自己或子目录下");
            }
        }
        for (int id : ids) {
            fileInfoMapper.moveFile(id, tarId);
        }
    }
    private boolean isSubFolder(int tarId, int folderId) {
        Integer parentId = tarId;
        while (parentId != null && parentId != 0) {
            if (parentId == folderId) {
                return true;
            }
            parentId = fileInfoMapper.getParentId(parentId);
        }
        return false;
    }


    public List<FilesVO> search(String token, String name) {
        int userId = TokenUtil.parseToken(token);
        return fileInfoMapper.search(userId, name);
    }
}
