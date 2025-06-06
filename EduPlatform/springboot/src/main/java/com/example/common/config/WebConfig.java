package com.example.common.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry; // 新增
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private JwtInterceptor jwtInterceptor;

    // 加自定义拦截器JwtInterceptor，设置拦截规则
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/")
                .excludePathPatterns("/login", "/register", "/files/**", "/role/selectAll");
    }

    // 新增：配置跨域请求（CORS）
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 对所有接口生效
                .allowedOrigins("http://localhost:4173", "http://localhost:5173") // 允许的前端地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 支持的方法
                .allowedHeaders("*") // 允许的请求头
                .allowCredentials(true); // 是否允许发送 Cookie（如 JWT 的 token 存在 cookie 中时需要）
    }
}