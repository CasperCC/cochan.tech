package com.cochan.blog.controller;

import com.cochan.blog.common.RedisUtil;
import com.cochan.blog.common.ResponseUtil;
import com.cochan.blog.common.VerifyUtil;
import com.cochan.blog.service.implement.MailServiceImpl;
import com.cochan.blog.service.implement.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author cochan
 */
@RestController
@RequestMapping(value = "/login", produces = "application/json; charset=UTF-8")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private MailServiceImpl mailService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${redisKey.key.img_code}")
    private String IMG_CODE;

    @Value("${redisKey.key.email_code}")
    private String EMAIL_CODE;

    @Value("${redisKey.cachetime.second}")
    private int cacheSecond;

    private String imgToken;

    private String mailToken;

    /**
     * 图形验证码接口
     * @param response http响应
     */
    @RequestMapping(value = "/getcode")
    public void getCode(HttpServletResponse response) {
        imgToken = UUID.randomUUID().toString().replaceAll("-","");
        VerifyUtil verifyUtil = new VerifyUtil();
        String imgCode = verifyUtil.getRandcode(response);
        stringRedisTemplate.opsForValue().set(IMG_CODE+imgToken, imgCode);
        stringRedisTemplate.expire(IMG_CODE+imgToken, cacheSecond, TimeUnit.SECONDS);
    }

    /**
     * 登录检查接口
     * @param request 以POST方式接收前端的值
     * @return 处理用户输入的信息并返回结果
     */
    @PostMapping(value = "/checklogin")
    public String checkLogin(HttpServletRequest request) {
        String username = request.getParameter("userTag");
        String password = request.getParameter("password");
        String imgCode = request.getParameter("imgCode");
        String paraImgToken = request.getParameter("paraImgToken");
        String imgCodeKey = IMG_CODE + paraImgToken.substring(7);

        if (username == null || password == null || imgCode == null) {
            return ResponseUtil.missParams();
        }

        return userService.checkLogin(username, password, imgCode, imgCodeKey);
    }

    /**
     * 获取图形验证码token
     * @return 以json格式返回token
     */
    @PostMapping(value = "/getimgtoken")
    public String getImgToken() {
        return ResponseUtil.success("token: "+imgToken);
    }

    /**
     * 检查注册信息是否合法
     * @param request
     * @return
     */
    @PostMapping(value = "/checkregister")
    public String checkRegister(HttpServletRequest request) {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String nickname = request.getParameter("nickname");
        String password = request.getParameter("password");
        String emailCode = request.getParameter("emailCode");
        String paraEmailToken = request.getParameter("paraEmailToken");
        int is_administrator = Integer.parseInt(request.getParameter("is_administrator"));
        String emailCodeKey = EMAIL_CODE + paraEmailToken.substring(7);

        if (username == null || email == null || password == null || emailCode == null) {
            return ResponseUtil.missParams();
        }

        nickname = (nickname == null) ? username : nickname;
        is_administrator = (is_administrator == 0) ? is_administrator : 1;

        return userService.checkRegister(username, email, nickname, password, emailCode, emailCodeKey, is_administrator);
    }

    /**
     * 获取邮件验证码
     * @param request POST获得传参
     * @return 返回邮件发送状态
     */
    @PostMapping(value = "/getemailcode")
    public String getEmailCode(HttpServletRequest request) {
        mailToken = UUID.randomUUID().toString().replaceAll("-","");
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String nickname = request.getParameter("nickname");
        int code = (int)((Math.random()*9+1)*100000);
        String emailData = RedisUtil.stringtoJson(username, nickname, email, String.valueOf(code));

        stringRedisTemplate.opsForValue().set(EMAIL_CODE+mailToken, emailData);
        stringRedisTemplate.expire(EMAIL_CODE+mailToken, cacheSecond, TimeUnit.SECONDS);

        Map<String, Object> valueMap = new HashMap<>(3);
        valueMap.put("to", email);
        valueMap.put("title", "注册账户验证码");
        valueMap.put("operation", "注册账户");
        valueMap.put("username", nickname);
        valueMap.put("code", code);
        return mailService.sendSimpleMail(valueMap);
    }

    /**
     * 获取邮件验证码token
     * @return 以json格式返回token
     */
    @PostMapping(value = "/getemailtoken")
    public String getEmailToken() {
        return ResponseUtil.success("token: "+mailToken);
    }
}
