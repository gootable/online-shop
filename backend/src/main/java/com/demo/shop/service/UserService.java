package com.demo.shop.service;

import com.demo.shop.dto.LoginRequest;
import com.demo.shop.dto.RegisterRequest;
import com.demo.shop.entity.User;
import com.demo.shop.vo.UserVO;

public interface UserService {
    UserVO register(RegisterRequest request);
    String login(LoginRequest request);
    UserVO getCurrentUser(Long userId);
    UserVO getProfile(Long userId);
    void updateProfile(Long userId, UserVO vo);
    void changePassword(Long userId, String oldPassword, String newPassword);
    User getById(Long userId);
    User getByUsername(String username);
}
