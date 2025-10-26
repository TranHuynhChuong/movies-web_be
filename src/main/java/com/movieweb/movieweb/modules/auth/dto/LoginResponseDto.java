package com.movieweb.movieweb.modules.auth.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDto {
    private UserDto user;
    private String accessToken;
    private long expiresIn;
}