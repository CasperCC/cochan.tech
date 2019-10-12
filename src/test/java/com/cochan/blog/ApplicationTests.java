package com.cochan.blog;

import com.cochan.blog.service.implement.MailServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {
	@Autowired
	private JavaMailSender mailSender;

	@Test
	public void sendSimpleMail() throws Exception {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("administrator@coccuscc.cn");
		message.setTo("1034167213@qq.com");
		message.setSubject("主题：简单邮件");
		message.setText("测试邮件内容");

		mailSender.send(message);
	}
	@Test
	public void contextLoads() {
	}

	@Autowired
	private MailServiceImpl mailService;

	@Test
	public void sendMail() {
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("to", "1034167213@qq.com");
		valueMap.put("title", "注册账户验证码");
		valueMap.put("operation", "注册账户");
		valueMap.put("code", "645257");

		mailService.sendSimpleMail(valueMap);
	}

}
