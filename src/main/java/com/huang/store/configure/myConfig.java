package com.huang.store.configure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class myConfig implements WebMvcConfigurer {

    @Autowired
    private FileUploadConfig fileUploadConfig;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("upload");
        registry.addViewController("/login").setViewName("index");
        registry.addViewController("/test.html").setViewName("test");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源映射，使前端能够访问服务端的静态资源
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600); // 设置缓存时间1小时

        // 为了兼容性，也可以直接映射images路径
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/image/")
                .setCachePeriod(3600);

        // 添加更多的静态资源映射路径
        registry.addResourceHandler("/image/**")
                .addResourceLocations("classpath:/static/image/")
                .setCachePeriod(3600);

        // 直接映射book图片路径，方便访问
        registry.addResourceHandler("/book/**")
                .addResourceLocations("classpath:/static/image/book/")
                .setCachePeriod(3600);

        // 添加头像目录映射
        registry.addResourceHandler("/avatar/**")
                .addResourceLocations("classpath:/static/image/avatar/")
                .setCachePeriod(3600);
    }
}
