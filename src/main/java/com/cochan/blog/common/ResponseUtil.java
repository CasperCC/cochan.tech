package com.cochan.blog.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author cochan
 */
@Data
public class ResponseUtil {
    /**
     * 缺少参数
     */
    public static String missParams() {
        return resultReturn(-1, "缺少参数");
    }

    /**
     * token错误
     */
    public static String captchaError() {
        return resultReturn(-2, "验证码错误");
    }

    /**
     * 用户名或密码错误
     */
    public static String loginError() {
        return resultReturn(-3, "用户名或密码错误");
    }

    /**
     * 数据库错误
     */
    public static String dbError(String msg) {
        return resultReturn(-4, msg);
    }

    /**
     * 用户名已存在
     */
    public static String usernameExist() { return resultReturn(-5, "用户名已存在"); }

    /**
     * 邮箱已存在
     */
    public static String emailExist() { return resultReturn(-6, "邮箱已存在"); }

    /**
     * 账户锁定
     */
    public static String userLock() { return resultReturn(-7, "账户已锁定"); }

    /**
     * 邮件发送失败
     */
    public static String sendEmailFail() { return resultReturn(-8, "邮件发送失败"); }

    /**
     * 成功返回
     */
    public static String success() {
        return resultReturn(0, "ok");
    }
    public static String success(Object data) {
        return resultReturn(0, "ok", data);
    }

    /**
     * 通用封装返回
     */
    public static String resultReturn(Integer ret, String msg) {
        return JSON.toJSONString(new ResponseUtil(ret, msg));
    }
    public static String resultReturn(Integer ret, String msg, Object data) {
        return JSON.toJSONString(new ResponseUtil(ret, msg, data));
    }

    // 返回格式构造
    @JSONField(ordinal=1)
    private Integer ret;
    @JSONField(ordinal=2)
    private String msg;
    @JSONField(ordinal=3)
    private Object data;

    public ResponseUtil(Integer ret, String msg) {
        super();
        this.ret = ret;
        this.msg = msg;
    }

    public ResponseUtil(Integer ret, String msg, Object data) {
        super();
        this.ret = ret;
        this.msg = msg;
        this.data = data;
    }
}
