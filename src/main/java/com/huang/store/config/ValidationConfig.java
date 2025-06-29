package com.huang.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validator;

/**
 * 参数验证配置类
 * 
 * @author 系统管理员
 * @date 2024-01-01
 * @description 配置Bean Validation相关组件
 */
@Configuration
public class ValidationConfig {
    
    /**
     * 配置验证器
     */
    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }
    
    /**
     * 配置方法级别的参数验证
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator());
        return processor;
    }
}
