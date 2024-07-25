package com.self_back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FetchFileConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        本机开发版
        registry.addResourceHandler("/avatars/**").addResourceLocations("file:///E:/selfPan_Upload/avatars/");
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:///E:/selfPan_Upload/uploads/");
//        服务器部署版
//        registry.addResourceHandler("/avatars/**").addResourceLocations("file:///root/selfPan_Upload/avatars/");
//        registry.addResourceHandler("/uploads/**").addResourceLocations("file:///root/selfPan_Upload/uploads/");
    }
}
