package com.movieweb.movieweb.modules.auth.controller;

import com.movieweb.movieweb.common.dto.ApiResponse;
import com.movieweb.movieweb.common.dto.ResponseHelper;
import com.movieweb.movieweb.modules.auth.dto.LoginDto;
import com.movieweb.movieweb.modules.auth.service.AuthService;
import com.movieweb.movieweb.modules.auth.annotations.PublicEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/xac-thuc")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PublicEndpoint()
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginDto dto) {
        String token = authService.login(dto.getUsername(), dto.getPassword());

        return ResponseEntity.ok(ResponseHelper.success("Đăng nhập thành công", token));
    }
}
