package com.self_back.service;

import com.self_back.entity.po.FileInfo;
import com.self_back.entity.po.Share;
import com.self_back.entity.po.User;
import com.self_back.entity.vo.UserInfoVo;
import com.self_back.mapper.FileInfoMapper;
import com.self_back.mapper.OutShareMapper;
import com.self_back.mapper.UserMapper;
import com.self_back.utils.Constant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OutShareService {
    @Autowired
    private OutShareMapper outShareMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FileInfoMapper fileInfoMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    public Share getInfoByLink(String link) {
        Share share = outShareMapper.getShareByLink(link);
        if (share == null) {
            throw new RuntimeException("分享链接不存在");
        }
        return share;
    }

    public UserInfoVo getUserInfo(int userId) {
        UserInfoVo userInfoVo = new UserInfoVo();
        String avatarPath = Constant.AVATAR_PATH+userId+".jpg";
        Path path = Paths.get(avatarPath);
        String avatar;
        if (!path.toFile().exists()) {
            avatar = "default.jpg";
        } else {
            avatar = userId+".jpg";
        }
        User user = userMapper.searchUserById(userId);
        userInfoVo.setUsername(user.getUsername());
        userInfoVo.setAvatar(avatar);
        return userInfoVo;
    }

    public FileInfo getFileInfo(int fileId) {
        return outShareMapper.getFileInfo(fileId);
    }
    public void changeFolder(int userId, int tarId, int[] ids) {
        for (int id : ids) {
            // 递归复制文件或文件夹
            recursiveCopy(id, tarId, userId);
        }
    }

    private void recursiveCopy(int sourceId, int newParentId, int newUserId) {
        // 首先复制文件或文件夹本身
        FileInfo fileInfo = fileInfoMapper.getFile(sourceId);
        fileInfo.setUserId(newUserId);
        fileInfo.setPid(newParentId);
        fileInfo.setCreateTime(new Date());
        fileInfo.setLastUpdateTime(new Date());
        int newId = outShareMapper.copyFile(fileInfo);
        userMapper.updateUseSpace(newUserId, fileInfo.getSize());
        String userInfoKey = "userInfo:" + newUserId;
        Map<String,String> updateInfo = new HashMap<>();
        updateInfo.put("update_userInfo",userInfoKey);
        rabbitTemplate.convertAndSend("selfpan","sql_and_redis_key",updateInfo);
        // 如果是文件夹，递归复制其所有子文件和子文件夹
        if (fileInfo.getType() == 0) {
            List<Integer> children = outShareMapper.getChildren(sourceId);
            for (int childId : children) {
                recursiveCopy(childId, newId, newUserId);
            }
        }
    }
}
