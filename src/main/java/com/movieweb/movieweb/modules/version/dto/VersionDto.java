package com.movieweb.movieweb.modules.version.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VersionDto {
    @NotBlank(message = "Tên phiên bản không được để trống")
    private String name;
}