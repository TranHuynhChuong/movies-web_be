package com.movieweb.movieweb.modules.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerDto {
    @NotBlank(message = "Tên máy chủ không được để trống")
    private String name;
}