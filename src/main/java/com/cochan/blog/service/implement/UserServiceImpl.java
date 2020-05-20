package com.cochan.blog.service.implement;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cochan.blog.common.HashUtil;
import com.cochan.blog.common.ResponseUtil;
import com.cochan.blog.entity.UserEntity;
import com.cochan.blog.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author cochan
 */
@Service
public class UserServiceImpl {

    @Resource
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${redisKey.key.img_code}")
    private String IMG_CODE;


    private long LOCKTIME = 300;

    public String checkLogin(String username, String password, String imgCode, String imgCodeKey) {
        if (imgCodeKey == null) {
            return ResponseUtil.captchaError();
        }
        String redisImgCode = stringRedisTemplate.opsForValue().get(imgCodeKey);
        // 数据库错误
        if (redisImgCode == null) {
            return ResponseUtil.dbError("数据库错误0");
        }
        // 验证码错误
        if (!imgCode.toLowerCase().equals(redisImgCode.toLowerCase())) {
            return ResponseUtil.captchaError();
        }
        
        UserEntity userInfo = userMapper.queryUserByUsername(username);
        if (userInfo == null) {
            return ResponseUtil.loginError();
        }
        if (userInfo.getStatus() != 0 && userInfo.getUpdated() > System.currentTimeMillis() / 1000 - LOCKTIME) {
            return ResponseUtil.userLock();
        }
        long uid = userInfo.getUid();
        long created = userInfo.getCreated();
        String salt = userInfo.getSalt();
        String hashPassword = HashUtil.getHashPass(uid, username, password, salt, created);
        if (hashPassword == null) {
            return  ResponseUtil.dbError("数据库错误1");
        }
        if (!hashPassword.equals(userInfo.getHashpassword())){
            userInfo.setPassword_wrong_time(userInfo.getPassword_wrong_time()+1);
            if (userInfo.getPassword_wrong_time() == 5) {
                userInfo.setStatus(1);
            }
            userInfo.setUpdated(System.currentTimeMillis() / 1000);
            int affectedRows = userMapper.lockUser(userInfo);
            if (affectedRows != 1) {
                return ResponseUtil.dbError("数据库错误2");
            }
            return ResponseUtil.loginError();
        }
        if (userInfo.getStatus() != 0 || userInfo.getPassword_wrong_time() != 0) {
            userInfo.setStatus(0);
            userInfo.setPassword_wrong_time(0);
        }
        userInfo.setUpdated(System.currentTimeMillis() / 1000);
        int affectedRows = userMapper.lockUser(userInfo);
        if (affectedRows != 1) {
            return ResponseUtil.dbError("数据库错误3");
        }
        stringRedisTemplate.opsForValue().getOperations().delete(imgCodeKey);
        return ResponseUtil.success();
    }

    public String checkRegister(String username, String email, String nickname, String password, String emailCode, String emailCodeKey, int is_administrator) {
        if (emailCodeKey == null) {
            return ResponseUtil.captchaError();
        }
    	String emailData = stringRedisTemplate.opsForValue().get(emailCodeKey);
    	JSONObject jsonObject=JSON.parseObject(emailData);
    	String redisUsername = jsonObject.getString("username");
    	String redisNickname = jsonObject.getString("nickname");
    	String redisEmailCode = jsonObject.getString("code");
    	String redisEmail = jsonObject.getString("email");

        // 数据库错误
        if (redisEmailCode == null || redisEmail == null) {
            return ResponseUtil.dbError("数据库错误0"+emailCodeKey);
        }
        // 验证码错误
        if (!(redisUsername.equals(username) && redisNickname.equals(nickname) && emailCode.equals(redisEmailCode) && email.equals(redisEmail))) {
            return ResponseUtil.captchaError();
        }
        stringRedisTemplate.opsForValue().getOperations().delete(emailCodeKey);
    	
    	UserEntity userInfoByUsername = userMapper.queryUserByUsername(username);
        UserEntity userInfoByEmail = userMapper.queryUserByEmail(email);
        
        if (userInfoByUsername != null) {
            return ResponseUtil.usernameExist();
        }
        if (userInfoByEmail != null) {
            return ResponseUtil.emailExist();
        }
        String salt = UUID.randomUUID().toString().replaceAll("-","");

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setSalt(salt);
        user.setStatus(0);
        user.setIs_administrator(is_administrator);
        user.setCreated(System.currentTimeMillis() / 1000);
        user.setUpdated(System.currentTimeMillis() / 1000);

        int affectedRows = userMapper.insertUser(user);

        if (affectedRows != 1) {
            return ResponseUtil.dbError("新增用户信息失败");
        }
        String hashPassword = HashUtil.getHashPass(user.getUid(), username, password, salt, user.getCreated());
        if (hashPassword == null) {
            return ResponseUtil.dbError("未返回uid");
        }
        user.setHashpassword(hashPassword);
        user.setUpdated(System.currentTimeMillis() / 1000);
        affectedRows = userMapper.updateUserHashPassword(user);
        if (affectedRows != 1) {
            return ResponseUtil.dbError("新增用户密码失败");
        }

        return ResponseUtil.success();
    }
}
