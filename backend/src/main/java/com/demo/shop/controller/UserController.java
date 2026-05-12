package com.demo.shop.controller;

import com.demo.shop.common.Result;
import com.demo.shop.dto.UpdatePasswordRequest;
import com.demo.shop.security.UserContext;
import com.demo.shop.service.UserService;
import com.demo.shop.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public Result<UserVO> getProfile() {
        Long userId = UserContext.getCurrentUserId();
        UserVO vo = userService.getProfile(userId);
        return Result.ok(vo);
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UserVO vo) {
        Long userId = UserContext.getCurrentUserId();
        userService.updateProfile(userId, vo);
        return Result.ok();
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        Long userId = UserContext.getCurrentUserId();
        userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return Result.ok();
    }
}
