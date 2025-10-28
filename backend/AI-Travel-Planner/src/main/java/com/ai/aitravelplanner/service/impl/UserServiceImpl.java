package com.ai.aitravelplanner.service.impl;

import com.ai.aitravelplanner.entity.User;
import com.ai.aitravelplanner.mapper.UserMapper;
import com.ai.aitravelplanner.mapper.UserPreferenceMapper;
import com.ai.aitravelplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserPreferenceMapper userPreferenceMapper;

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

    @Override
    public Map<String, Object> getUserPreferences(Long userId) {
        List<String> prefs = userMapper.getPreferencesByUserId(userId);
        String travelStyle = userMapper.getTravelStyleByUserId(userId);

        System.out.println("travelStyle: " + travelStyle);
        Map<String, Object> result = new HashMap<>();
        result.put("preferences", prefs);
        result.put("travelStyle", travelStyle);

        return result;
    }

    @Override
    public boolean saveUserPreferences(Long userId, List<String> preferences, String travelStyle) {
        // 删除旧数据
        userPreferenceMapper.deleteByUserId(userId);

        // 插入新数据
        if (preferences != null && !preferences.isEmpty()) {
            for (String pref : preferences) {
                userPreferenceMapper.insert(userId, pref, travelStyle);
            }
        } else {
            // 如果没有选择偏好，仍插入 travelStyle
            userPreferenceMapper.insert(userId, null, travelStyle);
        }
        return true;
    }
}
