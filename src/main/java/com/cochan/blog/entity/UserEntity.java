package com.cochan.blog.entity;

import lombok.Data;

/**
 * @author cochan
 */
@Data
public class UserEntity {

    private Long uid;
    private String username;
    private String nickname;
    private String email;
    private String salt;
    private String hashpassword;
    private Integer password_wrong_time;
    private Integer status;
    private Integer is_administrator;
    private Long created;
    private Long updated;

}
