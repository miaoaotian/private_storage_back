package com.self_back.mapper;

import com.self_back.entity.po.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("select * from user where email = #{email}")
    User searchUser(String email);
    @Select("select * from user where id = #{id}")
    User searchUserById(int id);
    @Update("update user set password=#{newpass} where id = #{id}")
    void changepass(@Param("newpass") String newpass, @Param("id") int id);
    @Insert("insert into user(username,password,email) values (#{username},#{password},#{email})")

    void insertUser(@Param("username") String username,@Param("password") String password,@Param("email") String email);
    @Update("update user set useSpace = useSpace + #{size} where id = #{id}")
    void updateUseSpace(@Param("id") int id, @Param("size") long size);

}
