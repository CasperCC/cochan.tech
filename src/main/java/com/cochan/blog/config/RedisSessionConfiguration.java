package com.cochan.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author cochan
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 7200)
public class RedisSessionConfiguration {
}
