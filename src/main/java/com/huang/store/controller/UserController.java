package com.huang.store.controller;

import com.huang.store.entity.user.Address;
import com.huang.store.entity.user.User;
import com.huang.store.service.UserServiceImp;
import com.huang.store.service.imp.AddressService;
import com.huang.store.service.imp.UserService;
import com.huang.store.util.ResultUtil;
import com.huang.store.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.huang.store.configure.FileUploadConfig;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.huang.store.util.JwtTokenUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import javax.servlet.http.HttpServletRequest;
import com.huang.store.util.UploadUtil;

@Controller
@ResponseBody
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    @Qualifier("firstUser")
    UserService userService;

    @Autowired
    @Qualifier("firstAddress")
    AddressService addressService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileUploadConfig fileUploadConfig;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

//  ============= 注册 =====================

    /**
     * 验证账号是否已被注册
     * @param account 用户账号（邮箱）
     * @return 验证结果
     */
    @GetMapping(value = "/user/accountVerify")
    public Map<String, Object> accountVerify(@RequestParam(value = "account") String account) {
        logger.info("验证账号是否已注册: {}", account);

        // 参数验证
        if (ValidationUtil.isEmpty(account)) {
            logger.warn("账号参数为空");
            return ResultUtil.resultCode(400, "账号不能为空");
        }
        if (!ValidationUtil.isValidEmail(account)) {
            logger.warn("账号格式不正确: {}", account);
            return ResultUtil.resultCode(400, "账号格式不正确");
        }

        User user = userService.getUser(account);
        if (user != null) {
            logger.warn("账号已被注册: {}", account);
            return ResultUtil.resultCode(500, "该账号已被注册！");
        }

        logger.info("账号可以注册: {}", account);
        return ResultUtil.resultCode(200, "该账号可以注册");
    }

    /**
     * 用户注册
     * @param account 用户账号（邮箱）
     * @param password 用户密码
     * @return 注册结果
     */
    @GetMapping(value = "/user/register")
    public Map<String, Object> registerUser(@RequestParam(value = "account") String account,
                                           @RequestParam(value = "password") String password) {
        logger.info("开始用户注册: {}", account);

        // 参数验证
        if (account == null || account.trim().isEmpty()) {
            logger.warn("账号参数为空");
            return ResultUtil.resultCode(400, "账号不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            logger.warn("密码参数为空");
            return ResultUtil.resultCode(400, "密码不能为空");
        }
        if (password.length() < 6) {
            logger.warn("密码长度不足");
            return ResultUtil.resultCode(400, "密码长度不能少于6位");
        }

        // 检查账号是否已存在
        User existingUser = userService.getUser(account);
        if (existingUser != null) {
            logger.warn("账号已被注册: {}", account);
            return ResultUtil.resultCode(500, "该账号已被注册！");
        }

        // 创建新用户
        User user = new User();
        user.setAccount(account);
        user.setPassword(passwordEncoder.encode(password));
        user.setManage(false);
        user.setEnable(true);
        user.setRegisterTime(new Timestamp(new Date().getTime()));

        int result = userService.addUser(user);
        if (result <= 0) {
            logger.error("用户注册失败: {}", account);
            return ResultUtil.resultCode(500, "注册失败，请稍后重试");
        }

        logger.info("用户注册成功: {}", account);
        return ResultUtil.resultCode(200, "注册成功");
    }

//    =====================对用户地址的操作=====================================
    /**
     *
     * @return 某个用户的地址列表
     */
    @ResponseBody
    @GetMapping("/getUserAddress")
    public Map<String,Object> getUserAddress(@RequestParam("account")String account){
        Map<String,Object> map = new HashMap<>();
        logger.info("获取用户地址列表: {}", account);
        List<Address> addressList = addressService.addressList(account);
        logger.debug("找到{}个地址", addressList.size());
        map.put("addressList",addressList);
        return ResultUtil.resultSuccess(map);
    }

    /**
     * 添加地址
     * @param address
     * @return
     */
    @ResponseBody
    @PostMapping("/addUserAddress")
    public Map<String,Object> addUserAddress(@RequestBody Address address){
        int result = addressService.addAddress(address);
        if(result>0){
            return ResultUtil.resultCode(200,"添加地址成功");
        }
        return ResultUtil.resultCode(500,"添加地址失败");
    }


    /**
     * 删除地址
     * @param id
     * @return 删除信息
     */
    @ResponseBody
    @GetMapping("/delUserAddress")
    public Map<String,Object> delUserAddress(@RequestParam("id")int id){
        int result = addressService.deleteAddress(id);
        if(result>0){
            return ResultUtil.resultCode(200,"删除地址成功");
        }
        return ResultUtil.resultCode(500,"删除地址失败");
    }

    /**
     * 修改地址
     * @param address
     * @return 修改信息
     */
    @ResponseBody
    @PostMapping("/modifyUserAddress")
    public Map<String,Object> modifyUserAddress(@RequestBody Address address){
        int result = addressService.modifyAddress(address);
        if(result>0){
            return ResultUtil.resultCode(200,"修改地址成功");
        }
        return ResultUtil.resultCode(500,"修改地址失败");
    }

//    =============================用户列表管理==================================

    /**
     * 按页得到用户列表
     * @param page
     * @param pageSize
     * @return
     */
    @ResponseBody
    @GetMapping("/getUserList")
    public Map<String,Object> getUserList(@RequestParam(value = "page")int page,
                                          @RequestParam(value = "pageSize")int pageSize){
        List<User> userList = userService.getUsersByPage(page, pageSize);
        int total = userService.count();
        Map<String,Object> map = new HashMap<>();
        map.put("userList",userList);
        map.put("total",total);
        return ResultUtil.resultSuccess(map);
    }

    /**
     * 修改用户的状态(禁用或没被禁用)
     * @param id
     * @param status
     * @return
     */
    @ResponseBody
    @GetMapping("/modifyUserStatus")
    public Map<String,Object> modifyUserStatus(@RequestParam(value = "id")int id,
                                               @RequestParam(value = "status")boolean status){
        User user = new User();
        user.setId(id);
        user.setEnable(true);
        logger.info("修改用户状态: id={}, status={}", id, status);
        if(userService.updateUser(user)>0){
            return ResultUtil.resultCode(200,"修改成功");
        }
        return ResultUtil.resultCode(500,"修改失败");
    }

//    =================================用户个人信息管理================================

    /**
     * 得到一个用户的个人信息
     * @param account
     * @return
     */
    @ResponseBody
    @GetMapping("/getUserInfo")
    public Map<String,Object> getUserInfo(@RequestParam(value = "account")String account){
        User user = userService.getUser(account);
        Map<String,Object> map = new HashMap<>();
        map.put("user",user);
        return ResultUtil.resultSuccess(map);
    }

    /**
     * 修改用户的密码
     * @param account
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @ResponseBody
    @GetMapping("/modifyUserPwd")
    public Map<String,Object> modifyUserPwd(@RequestParam(value = "account")String account,
                                            @RequestParam(value = "oldPassword")String oldPassword,
                                            @RequestParam(value = "newPassword")String newPassword){
        User user = userService.getUser(account);
        System.out.println("=======user account========:"+account+"=========");
        System.out.println("=====开始修改密码======");
        System.out.println("=====前端传过来的旧密码：====："+oldPassword+"=====");
        System.out.println("=====数据库中的密码：================"+user.getPassword()+"====");
        if(passwordEncoder.matches(oldPassword,user.getPassword())){
            System.out.println("====说明密码匹配正确====");
            newPassword = passwordEncoder.encode(newPassword);
            if(userService.updatePwd(account,newPassword)>0){
                return ResultUtil.resultCode(200,"修改密码成功");
            }
        }
        return ResultUtil.resultCode(500,"修改密码错误!请确认你输入了原先正确的密码");
    }

    /**
     * 用户头像上传（从token获取用户信息）
     */
    @PostMapping("/user/uploadAvatar")
    public Map<String, Object> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        // 从token获取用户账号
        String account = getUserAccountFromToken(request);
        if (account == null) {
            return ResultUtil.resultCode(401, "用户未登录");
        }
        if (file.isEmpty()) {
            return ResultUtil.resultCode(400, "文件为空");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResultUtil.resultCode(400, "只允许上传图片文件");
        }
        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + ext;
        String path = fileUploadConfig.getAvatarUploadPath();
        String imgName = UploadUtil.uploadFile(file, path);
        if ("上传失败".equals(imgName)) {
            return ResultUtil.resultCode(500, "文件上传失败");
        }
        String imgUrl = fileUploadConfig.getAvatarPath() + imgName;
        int update = userService.updateImg(account, imgUrl);
        if (update > 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("imgUrl", imgUrl);
            return ResultUtil.resultSuccess(data);
        } else {
            return ResultUtil.resultCode(500, "头像更新失败");
        }
    }

    /**
     * 从Token中获取用户账号
     */
    private String getUserAccountFromToken(HttpServletRequest request) {
        try {
            // 方法1：从SecurityContext中获取（推荐）
            org.springframework.security.core.Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getName())) {
                return authentication.getName();
            }
            // 方法2：直接从Authorization header获取token（兼容现有系统）
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && !authHeader.isEmpty()) {
                return jwtTokenUtil.getUserNameFromToken(authHeader);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/updateUserInfo")
    public Map<String, Object> updateUserInfo(@RequestBody Map<String, Object> userInfo, HttpServletRequest request) {
        try {
            String userAccount = getUserAccountFromToken(request);
            if (userAccount == null) {
                return ResultUtil.resultCode(401, "用户未登录");
            }

            logger.info("更新用户信息: userAccount={}, userInfo={}", userAccount, userInfo);

            // 获取当前用户
            User user = userService.getUser(userAccount);
            if (user == null) {
                return ResultUtil.resultCode(404, "用户不存在");
            }

            // 更新用户信息
            if (userInfo.containsKey("name")) {
                user.setName((String) userInfo.get("name"));
            }
            if (userInfo.containsKey("gender")) {
                user.setGender((String) userInfo.get("gender"));
            }
            if (userInfo.containsKey("info")) {
                user.setInfo((String) userInfo.get("info"));
            }

            // 保存更新
            int result = userService.updateUser(user);
            if (result > 0) {
                return ResultUtil.resultCode(200, "用户信息更新成功");
            } else {
                return ResultUtil.resultCode(500, "更新失败");
            }

        } catch (Exception e) {
            logger.error("更新用户信息失败", e);
            return ResultUtil.resultCode(500, "更新失败");
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/updatePassword")
    public Map<String, Object> updatePassword(@RequestBody Map<String, Object> passwordInfo, HttpServletRequest request) {
        try {
            String userAccount = getUserAccountFromToken(request);
            if (userAccount == null) {
                return ResultUtil.resultCode(401, "用户未登录");
            }

            String oldPassword = (String) passwordInfo.get("oldPassword");
            String newPassword = (String) passwordInfo.get("newPassword");

            if (oldPassword == null || newPassword == null) {
                return ResultUtil.resultCode(400, "密码不能为空");
            }

            logger.info("修改密码: userAccount={}", userAccount);

            // 获取当前用户
            User user = userService.getUser(userAccount);
            if (user == null) {
                return ResultUtil.resultCode(404, "用户不存在");
            }

            // 验证旧密码
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return ResultUtil.resultCode(400, "当前密码错误");
            }

            // 更新密码
            user.setPassword(passwordEncoder.encode(newPassword));
            int result = userService.updateUser(user);

            if (result > 0) {
                return ResultUtil.resultCode(200, "密码修改成功");
            } else {
                return ResultUtil.resultCode(500, "密码修改失败");
            }

        } catch (Exception e) {
            logger.error("修改密码失败", e);
            return ResultUtil.resultCode(500, "密码修改失败");
        }
    }
}
