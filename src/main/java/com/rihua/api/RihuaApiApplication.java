package com.rihua.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Rihua Community Platform API Application
 * 
 * <p>日華コミュニティプラットフォーム向けのSpring Boot REST APIアプリケーション</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
public class RihuaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RihuaApiApplication.class, args);
    }
}