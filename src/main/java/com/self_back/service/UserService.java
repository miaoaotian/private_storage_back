package com.self_back.service;
import com.self_back.Exception.BusinessException;
import com.self_back.Exception.CodeErrException;
import com.self_back.entity.vo.UserInfoVo;
import com.self_back.mapper.UserMapper;
import com.self_back.entity.dto.ChangePassDTO;
import com.self_back.entity.po.User;
import com.self_back.entity.dto.UserDTO;
import com.self_back.utils.Constant;
import com.self_back.utils.TokenUtil;
import com.self_back.Exception.NoUserException;
import com.self_back.Exception.PassErrException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    public RedisTemplate redisTemplate;
    public String doLogin(UserDTO userDTO) {
        String email = userDTO.getEmail();
        String password = DigestUtils.md5DigestAsHex(userDTO.getPassword().getBytes()).toUpperCase()+ Constant.pass_end;

        String code = userDTO.getVerification().toLowerCase();

        String redisKey ="captcha_code:" + userDTO.getCaptchaUUID();
        String realCode = (String) redisTemplate.opsForValue().get(redisKey);
        User user = userMapper.searchUser(email);

        if (user == null){throw new NoUserException("未找到该用户");}
        if (!user.getPassword().equals(password)) {throw new PassErrException("密码错误");}
        if (!code.equals(realCode.toLowerCase())) {throw new CodeErrException("验证码错误");}

        String userInfoKey = "userInfo:" + user.getId();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", user.getUsername());
        userInfo.put("id", user.getId());
        userInfo.put("useSpace", user.getUseSpace());
        userInfo.put("totalSpace", user.getTotalSpace());
        redisTemplate.opsForHash().putAll(userInfoKey, userInfo);

        String token = TokenUtil.generateToken(user.getId());
        return token;
    }
    public void doRegister(User user) {
        String email = user.getEmail();
        String username = user.getUsername();
        String password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes()).toUpperCase()+ Constant.pass_end;
        User tempUser = userMapper.searchUser(email);
        if (tempUser != null) {throw new BusinessException("该邮箱已被注册");}
        String key = "code_"+email;
        String nowCode = (String) redisTemplate.opsForValue().get(key);
        if (nowCode == null) {throw new CodeErrException("验证码已过期");}
        if (!user.getEmailVerification().equals(nowCode)) {throw new CodeErrException("验证码错误");}
        userMapper.insertUser(username,password,email);
    }
    public void changepass(String token, ChangePassDTO changePassDTO) {
        int userId = TokenUtil.parseToken(token);
        String oldpass = DigestUtils.md5DigestAsHex(changePassDTO.getOldpass().getBytes()).toUpperCase()+ Constant.pass_end;
        String newpass = DigestUtils.md5DigestAsHex(changePassDTO.getNewpass().getBytes()).toUpperCase()+ Constant.pass_end;
        User user = userMapper.searchUserById(userId);
        if (!oldpass.equals(user.getPassword())) {throw new PassErrException("旧密码错误");}
        userMapper.changepass(newpass,userId);
    }

    public UserInfoVo getUserInfo(String token) {
        int userId = TokenUtil.parseToken(token);
        String avatarPath = Constant.AVATAR_PATH+userId+".jpg";
        Path path = Paths.get(avatarPath);
        String avatar;
        if (!path.toFile().exists()) {
            avatar = "default.jpg";
        } else {
            avatar = userId+".jpg";
        }
        String userInfoKey = "userInfo:" + userId;
        Map<String, Object> userInfo;
        if (redisTemplate.hasKey(userInfoKey)) {
            userInfo = (Map) redisTemplate.opsForHash().entries(userInfoKey);
        } else {
            User user = userMapper.searchUserById(userId);
            userInfo = new HashMap<>();
            userInfo.put("username", user.getUsername());
            userInfo.put("id", user.getId());
            userInfo.put("useSpace", user.getUseSpace());
            userInfo.put("totalSpace", user.getTotalSpace());
            redisTemplate.opsForHash().putAll(userInfoKey, userInfo);
        }

        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setUsername((String) userInfo.get("username"));
        userInfoVo.setAvatar(avatar);
        userInfoVo.setUseSpace((Long) userInfo.get("useSpace"));
        userInfoVo.setTotalSpace((Long) userInfo.get("totalSpace"));
        return userInfoVo;
    }

    public void changeAvatar(String token, MultipartFile avatar) {
        int userId = TokenUtil.parseToken(token);
        String avatarPath = Constant.AVATAR_PATH+userId+".jpg";
        try {
            avatar.transferTo(new File(avatarPath));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("头像上传失败");
        }
    }
}
