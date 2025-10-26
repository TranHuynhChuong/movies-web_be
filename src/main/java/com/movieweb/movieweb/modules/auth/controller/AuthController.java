package com.movieweb.movieweb.modules.auth.controller;

import com.movieweb.movieweb.common.dto.ApiResponse;
import com.movieweb.movieweb.common.dto.ResponseHelper;
import com.movieweb.movieweb.modules.auth.dto.LoginDto;
import com.movieweb.movieweb.modules.auth.dto.LoginResponseDto;
import com.movieweb.movieweb.modules.auth.dto.UserDto;
import com.movieweb.movieweb.modules.auth.entity.Role;
import com.movieweb.movieweb.modules.auth.service.AuthService;
import com.movieweb.movieweb.modules.auth.annotations.PublicEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PublicEndpoint()
    @PostMapping("/login-admin")
    public ResponseEntity<ApiResponse<LoginResponseDto>> loginAdmin(@Valid @RequestBody LoginDto dto) {
        LoginResponseDto logined = authService.login(dto.getUsername(), dto.getPassword(), Role.ADMIN);
        return ResponseEntity.ok(ResponseHelper.success("Đăng nhập thành công", logined ));
    }

    @PublicEndpoint()
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginDto dto) {
        LoginResponseDto logged = authService.login(dto.getUsername(), dto.getPassword(), Role.USER);
        return ResponseEntity.ok(ResponseHelper.success("Đăng nhập thành công", logged));
    }


    @PatchMapping("account/{id}")
    public ResponseEntity<ApiResponse<UserDto>> login(@PathVariable String id, @Valid @RequestBody LoginDto dto) {
        UserDto updated = authService.updateAccount(id, dto.getUsername(), dto.getPassword());
        return ResponseEntity.ok(ResponseHelper.success("Đăng nhập thành công", updated));
    }
}
