package com.ai.aitravelplanner.service.impl;

import com.ai.aitravelplanner.entity.User;
import com.ai.aitravelplanner.mapper.UserMapper;
import com.ai.aitravelplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            // 登录成功，不返回密码
            user.setPassword(null);
            return user;
        }
        return null;
    }

    @Override
    public boolean register(User user) {
        User existing = userMapper.findByUsername(user.getUsername());
        if (existing != null) {
            return false; // 用户名已存在
        }
        userMapper.insert(user);
        return true;
    }
}
