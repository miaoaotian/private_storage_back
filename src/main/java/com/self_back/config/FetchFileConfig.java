package com.self_back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FetchFileConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/avatars/**").addResourceLocations("file:///E:/selfPan_Upload/avatars/");
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:///E:/selfPan_Upload/uploads/");
    }
}
