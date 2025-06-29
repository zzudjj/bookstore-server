package com.huang.store.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 用户注册请求DTO
 * 
 * @author 系统管理员
 * @date 2024-01-01
 * @description 用户注册时的请求参数
 */
public class UserRegisterRequest {
    
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
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;
    
    /**
     * 用户姓名
     */
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String name;
    
    /**
     * 用户性别
     */
    private String gender;
    
    /**
     * 个人简介
     */
    @Size(max = 500, message = "个人简介长度不能超过500个字符")
    private String info;
    
    // 构造函数
    public UserRegisterRequest() {
    }
    
    public UserRegisterRequest(String account, String password) {
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getInfo() {
        return info;
    }
    
    public void setInfo(String info) {
        this.info = info;
    }
    
    @Override
    public String toString() {
        return "UserRegisterRequest{" +
                "account='" + account + '\'' +
                ", password='[PROTECTED]'" +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
