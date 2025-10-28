package com.ai.aitravelplanner.service;

import com.ai.aitravelplanner.entity.User;

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

}
