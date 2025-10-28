package com.ai.aitravelplanner.service;

import com.ai.aitravelplanner.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    User login(String username, String password);

    /**
     * 注册
     * @param user
     * @return
     */
    boolean register(User user);

    /**
     * 获取用户偏好
     * @param userId
     * @return
     */
    Map<String, Object> getUserPreferences(Long userId);

    /**
     * 保存用户偏好
     * @param userId
     * @param preferences
     * @param travelStyle
     */
    boolean saveUserPreferences(Long userId, List<String> preferences, String travelStyle);
}
