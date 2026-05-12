package com.demo.shop.controller;

import com.demo.shop.common.Result;
import com.demo.shop.dto.LoginRequest;
import com.demo.shop.dto.RegisterRequest;
import com.demo.shop.security.UserContext;
import com.demo.shop.service.UserService;
import com.demo.shop.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest request) {
        UserVO user = userService.register(request);
        return Result.ok(user);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request);
        Long userId = userService.getByUsername(request.getUsername()).getId();
        UserVO user = userService.getCurrentUser(userId);
        return Result.ok(Map.of("token", token, "user", user));
    }

    @GetMapping("/me")
    public Result<UserVO> me() {
        Long userId = UserContext.getCurrentUserId();
        UserVO user = userService.getCurrentUser(userId);
        return Result.ok(user);
    }
}
