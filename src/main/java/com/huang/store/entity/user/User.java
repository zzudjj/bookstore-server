package com.huang.store.entity.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 用户实体类
 *
 * @description: 用户类
 */
@Data
public class User {
    private int id;//用户编号
    private String account;//用邮箱注册
    private String password;
    private String name;
    private String gender;
    private String imgUrl;
    private String info;//个人简介
    private boolean manage;//是否为管理员
    private boolean enable;//是否禁用
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Timestamp registerTime;//注册时间
}
