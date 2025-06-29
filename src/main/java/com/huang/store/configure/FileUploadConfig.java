package com.huang.store.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 文件上传配置类
 * 统一管理文件上传相关的路径配置
 * 
 * @author: 系统管理员
 * @date: 2024-01-01
 * @description: 文件上传路径配置
 */
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {
    
    /**
     * 基础路径 - 现在使用服务端resources下的static文件夹
     */
    private String basePath = "src/main/resources/static/";

    /**
     * 图书图片相对路径
     */
    private String bookPath = "image/book/";

    /**
     * 书单封面相对路径
     */
    private String topicPath = "image/topic/";
    
    /**
     * 获取图书图片完整上传路径
     * @return 完整路径
     */
    public String getBookUploadPath() {
        return getAbsoluteBasePath() + bookPath;
    }

    /**
     * 获取书单封面完整上传路径
     * @return 完整路径
     */
    public String getTopicUploadPath() {
        return getAbsoluteBasePath() + topicPath;
    }

    /**
     * 获取文件的完整物理路径
     * @param relativePath 相对路径
     * @return 完整物理路径
     */
    public String getFullPath(String relativePath) {
        return getAbsoluteBasePath() + relativePath;
    }

    /**
     * 获取绝对基础路径
     * @return 绝对路径
     */
    private String getAbsoluteBasePath() {
        try {
            String actualBasePath = basePath;

            // 处理classpath:前缀
            if (basePath.startsWith("classpath:")) {
                // 移除classpath:前缀，使用项目相对路径
                actualBasePath = "src/main/resources/" + basePath.substring("classpath:".length());
            }

            // 获取项目根目录
            File projectRoot = new File(System.getProperty("user.dir"));
            File staticDir = new File(projectRoot, actualBasePath);

            // 确保目录存在
            if (!staticDir.exists()) {
                staticDir.mkdirs();
                System.out.println("创建目录: " + staticDir.getAbsolutePath());
            }

            return staticDir.getAbsolutePath() + File.separator;
        } catch (Exception e) {
            System.err.println("获取绝对路径失败: " + e.getMessage());
            // 如果获取失败，使用默认路径
            String fallbackPath = System.getProperty("user.dir") + File.separator + "src/main/resources/static/";
            File fallbackDir = new File(fallbackPath);
            if (!fallbackDir.exists()) {
                fallbackDir.mkdirs();
            }
            return fallbackPath;
        }
    }
    
    // Getter and Setter methods
    
    public String getBasePath() {
        return basePath;
    }
    
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
    
    public String getBookPath() {
        return bookPath;
    }
    
    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }
    
    public String getTopicPath() {
        return topicPath;
    }
    
    public void setTopicPath(String topicPath) {
        this.topicPath = topicPath;
    }
    
    @Override
    public String toString() {
        return "FileUploadConfig{" +
                "basePath='" + basePath + '\'' +
                ", bookPath='" + bookPath + '\'' +
                ", topicPath='" + topicPath + '\'' +
                ", bookUploadPath='" + getBookUploadPath() + '\'' +
                ", topicUploadPath='" + getTopicUploadPath() + '\'' +
                '}';
    }
}
