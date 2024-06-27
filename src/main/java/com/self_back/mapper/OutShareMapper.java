package com.self_back.mapper;

import com.self_back.entity.po.FileInfo;
import com.self_back.entity.po.Share;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OutShareMapper {

    Share getShareByLink(String shareUuid);
    @Select("select name,cover,type from fileinfo where id = #{fileId}")
    FileInfo getFileInfo(int fileId);
    @Insert("INSERT INTO fileinfo (user_id, md5, size, name, pid, cover, path, create_time, last_update_time, type, category, del) " +
            "VALUES (#{userId}, #{md5}, #{size}, #{name}, #{pid}, #{cover}, #{path}, #{createTime}, #{lastUpdateTime}, #{type}, #{category}, #{del})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int copyFile(FileInfo fileInfo);

    @Select("SELECT id FROM fileinfo WHERE pid = #{parentId}")
    List<Integer> getChildren(@Param("parentId") int parentId);
}
