package com.huang.store.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 用户登录请求DTO
 * 
 * @author 系统管理员
 * @date 2024-01-01
 * @description 用户登录时的请求参数
 */
public class UserLoginRequest {
    
    /**
     * 用户账号（邮箱）
     */
    @NotBlank(message = "账号不能为空")
    @Email(message = "账号格式不正确")
    private String account;
    
    /**
     * 用户密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    
    // 构造函数
    public UserLoginRequest() {
    }
    
    public UserLoginRequest(String account, String password) {
        this.account = account;
        this.password = password;
    }
    
    // Getter and Setter methods
    
    public String getAccount() {
        return account;
    }
    
    public void setAccount(String account) {
        this.account = account;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public String toString() {
        return "UserLoginRequest{" +
                "account='" + account + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}
