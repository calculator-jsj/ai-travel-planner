package com.ai.aitravelplanner.controller;

import com.ai.aitravelplanner.entity.User;
import com.ai.aitravelplanner.result.Result;
import com.ai.aitravelplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
}


