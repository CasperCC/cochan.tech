package com.cochan.blog.mapper;

import com.cochan.blog.entity.UserEntity;
import org.apache.ibatis.annotations.*;

/**
 * @author cochan
 */
@Mapper
public interface UserMapper {

    /**
     * 通过用户输入的用户名查询用户信息
     * @param username user input
     * @return UserEntity
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    UserEntity queryUserByUsername(String username);

    /**
     * 通过用户输入的邮箱查询用户信息
     * @param email 用户输入的邮箱
     * @return UserEntity
     */
    @Select("SELECT * FROM user WHERE email = #{email}")
    UserEntity queryUserByEmail(String email);

    /**
     * 新增用户信息
     * @param user user信息
     * @return affectedRows 影响行数
     */
    @Insert("INSERT INTO user (username, nickname, email, salt, status, is_administrator, created, updated) VALUES (#{username}, #{nickname}, #{email}, #{salt}, #{status}, #{is_administrator}, #{created}, #{updated})")
    @Options(useGeneratedKeys = true, keyProperty = "uid", keyColumn = "uid")
    Integer insertUser(UserEntity user);

    /**
     * 根据新增用户uid新增用户哈希密码
     * @param user 用户信息
     * @return affectedRows 影响行数
     */
    @Update("UPDATE user SET hashpassword = #{hashpassword}, updated = #{updated} WHERE uid = #{uid}")
    Integer updateUserHashPassword(UserEntity user);

    /**
     * 更新用户最后操作时间
     * @param user 用户信息
     * @return affectedRows 影响行数
     */
    @Update("UPDATE user SET updated = #{updated} WHERE uid = #{uid}")
    Integer updateUserUpdated(UserEntity user);

    /**
     * 封禁账户
     * @param user 用户信息
     * @return affectedRows 影响行数
     */
    @Update("UPDATE user SET password_wrong_time = #{password_wrong_time}, status = #{status}, updated = #{updated} WHERE uid = #{uid}")
    Integer lockUser(UserEntity user);
}
