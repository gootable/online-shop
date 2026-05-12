package com.demo.shop.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.shop.common.PageResult;
import com.demo.shop.common.Result;
import com.demo.shop.entity.User;
import com.demo.shop.mapper.UserMapper;
import com.demo.shop.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserMapper userMapper;

    @GetMapping
    public Result<PageResult<UserVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> p = new Page<>(page, size);
        Page<User> result = userMapper.selectPage(p,
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .orderByDesc(User::getCreatedAt));

        List<UserVO> vos = result.getRecords().stream().map(u -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(u, vo);
            return vo;
        }).collect(Collectors.toList());

        return Result.ok(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), vos));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setStatus(body.get("status"));
            userMapper.updateById(user);
        }
        return Result.ok();
    }

    @PutMapping("/{id}/role")
    public Result<Void> updateRole(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setRole(body.get("role"));
            userMapper.updateById(user);
        }
        return Result.ok();
    }
}
