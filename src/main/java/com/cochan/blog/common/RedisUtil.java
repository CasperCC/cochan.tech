package com.cochan.blog.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

@Data
public class RedisUtil {

	/**
     * 转换至json
     */
    public static String stringtoJson(String username, String nickname, String email, String code) {
        return JSON.toJSONString(new RedisUtil(username, nickname, email, code));
    }
    
	// 返回格式构造
    @JSONField(ordinal=1)
    private String username;
    @JSONField(ordinal=2)
    private String nickname;
    @JSONField(ordinal=3)
    private String email;
    @JSONField(ordinal=4)
    private String code;
    
    public RedisUtil(String username, String nickname, String email, String code) {
    	super();
    	this.username = username;
    	this.nickname = nickname;
    	this.email = email;
    	this.code = code;
    }
}
