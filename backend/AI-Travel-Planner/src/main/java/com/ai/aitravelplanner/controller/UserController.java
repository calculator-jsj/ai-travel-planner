package com.ai.aitravelplanner.controller;

import com.ai.aitravelplanner.entity.User;
import com.ai.aitravelplanner.result.Result;
import com.ai.aitravelplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public Result<User> login(@RequestParam String username,
                              @RequestParam String password) {
        User user = userService.login(username, password);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("用户名或密码错误");
        }
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        boolean ok = userService.register(user);
        if (ok) {
            return Result.success("注册成功");
        } else {
            return Result.error("用户名已存在");
        }
    }

    @GetMapping("/preference")
    public Result<Map<String, Object>> getPreferences(@RequestParam Long userId) {
        try {
            Map<String, Object> data = userService.getUserPreferences(userId);
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取用户偏好失败");
        }
    }

    @PostMapping("/preferences/save")
    public Result<?> savePreferences(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.parseLong(payload.get("userId").toString());
            List<String> preferences = (List<String>) payload.get("preferences");
            String travelStyle = (String) payload.get("travelStyle");

            userService.saveUserPreferences(userId, preferences, travelStyle);
            return Result.success("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("保存失败");
        }
    }
}


