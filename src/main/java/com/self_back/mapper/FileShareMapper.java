package com.self_back.mapper;

import com.self_back.entity.po.Share;
import com.self_back.entity.vo.ShareVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FileShareMapper {
    @Insert("INSERT INTO fileshare (file_id, user_id, code, valid_type, share_link, create_time, expire_time) " +
            "VALUES (#{share.fileId}, #{share.userId}, #{share.code}, #{share.validType}, #{share.shareLink}, #{share.createTime}, #{share.expireTime})")
    @Options(useGeneratedKeys = true, keyProperty = "share.id")
    void insertShare(@Param("share") Share share);

    List<ShareVO> getAllShares(int userId);
    @Delete("DELETE FROM fileshare WHERE id = #{shareId}")
    void cancelShare(int shareId);
}
