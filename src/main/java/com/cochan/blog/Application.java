package com.cochan.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author cochan
 */
@SpringBootApplication
@MapperScan("com.cochan.blog.mapper")
public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		System.setProperty("java.awt.headless","true");
		SpringApplication.run(Application.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}
}
