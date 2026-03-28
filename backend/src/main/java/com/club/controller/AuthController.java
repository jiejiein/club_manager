package com.club.controller;

import com.club.common.Result;
import com.club.dto.LoginDTO;
import com.club.dto.RegisterDTO;
import com.club.service.UserService;
import com.club.vo.LoginVO;
import com.club.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @GetMapping("/current")
    public Result<UserVO> getCurrentUser() {
        return Result.success(userService.getCurrentUser());
    }
}
