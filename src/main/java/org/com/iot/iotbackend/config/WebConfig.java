package org.com.iot.iotbackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    Logger logger = LoggerFactory.getLogger(WebConfig.class);
    private final FrontCorsUrlConfig frontCorsUrlConfig;

    public WebConfig(FrontCorsUrlConfig frontCorsUrlConfig) {
        this.frontCorsUrlConfig = frontCorsUrlConfig;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        logger.info("ENV URL ::: " + frontCorsUrlConfig.getUrl());
        registry.addMapping("/**")
                //Vue 배포서버, Swagger CORS 설정
                .allowedOrigins(
                        "http://localhost:8081",
                        "https://iot-front-end.vercel.app",
                        "https://moderate-tomasine-iot-toy-project-19bde098.koyeb.app/api/swagger-ui/index.html"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("Authorization", "*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}