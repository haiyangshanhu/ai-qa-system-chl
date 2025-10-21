package com.ai.qa.user.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 配置类
 * 用于配置API文档的生成和展示
 */
@Configuration
public class SwaggerConfig {

        /**
         * 自定义OpenAPI配置
         * 配置API文档的基本信息、服务器地址和安全认证方案
         * 
         * @return OpenAPI对象，包含所有API文档配置
         */
        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("用户管理API")
                                                .description("用户登录、注册、管理等接口")
                                                .version("1.0.0"))
                                .servers(java.util.Arrays.asList(
                                                new Server().url("http://localhost:8081")
                                                                .description("development environment")))
                                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                                .components(new Components()
                                                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                                                .name("bearerAuth")
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")
                                                                .description("JWT authentication token")));
        }
}