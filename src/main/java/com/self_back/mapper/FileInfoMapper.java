package com.self_back.mapper;

import com.self_back.entity.po.FileInfo;
import com.self_back.entity.vo.FilesVO;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface FileInfoMapper {
    @Select("select  md5, size, name, pid, cover, path, create_time as createTime, last_update_time as lastUpdateTime, type, category, del from fileinfo where id = #{id}")
    FileInfo getFile(@Param("id") int id);

    @Insert("insert into fileinfo(user_id, name, pid, create_time, last_update_time, type) values (#{fileInfo.userId}, #{fileInfo.name}, #{fileInfo.pid}, #{fileInfo.createTime}, #{fileInfo.lastUpdateTime}, #{fileInfo.type})")
    void addFolder(@Param("fileInfo") FileInfo fileInfo);
    @Select("select user_id as userId,size,type from fileinfo where id = #{id}")
    FileInfo getFileById(@Param("id") int id);
    List<FilesVO> getFileInfoByFileIdAndCategory(@Param("userId") int userId, @Param("id") Integer id, @Param("category") Integer category);
    @Update("update fileinfo set name = #{name},last_update_time = #{data} where id = #{id}")
    void updateFileName(@Param("id") int fileId, @Param("name") String newName,@Param("data") Date date);

    void markAsDeleted(int id);

    List<Integer> findChildrenById(int id);
    void markAsDeleted2(int id);

    List<Integer> findChildrenById2(int id);
    void markAsRefresh(int id);

    List<Integer> findChildrenById3(int id);
    void addFile(FileInfo fileInfo);
    @MapKey("id")
    List<Map<String, Object>> getPath(int id);
    @Select("select path from fileinfo where id = ${id}")
    String getPathById(int id);

    List<FilesVO> getFileInRecycle(int user_id);

    List<FilesVO> getAllFoldersById(@Param("userId") int userId,@Param("fileId") int fileId);

    @Update("UPDATE fileinfo SET pid = #{parentId} WHERE id = #{id}")
    void moveFile(@Param("id") int id, @Param("parentId") int parentId);

    @Select("SELECT pid FROM fileinfo WHERE id = #{id}")
    Integer getParentId(@Param("id") int id);

    @Select("SELECT path FROM fileinfo GROUP BY path HAVING COUNT(*) = COUNT(CASE WHEN del = 2 THEN 1 END)")
    List<String> findPathsToDelete();

    @Delete("DELETE FROM fileinfo WHERE path = #{path}")
    void deleteRecordsByPath(String path);

    @Delete("DELETE FROM fileinfo WHERE path IS NULL AND del = 2")
    void deleteRecordsWithoutPath();

    @Update("UPDATE fileinfo SET del = 2 WHERE del = 1 AND DATEDIFF(NOW(), last_update_time) > 10")
    void updateDeletedRecords();
}
