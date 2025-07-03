package com.salon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${upload.recipe-path}")
    private String recipePath;

    @Value("${upload.post-path}")
    private String postPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/recipeImg/**")
                .addResourceLocations(recipePath);

        registry.addResourceHandler("/postImg/**")
                .addResourceLocations(postPath);
        registry
                .addResourceHandler("/upload/**")
                .addResourceLocations("file:///C:/upload/");
    }


}


