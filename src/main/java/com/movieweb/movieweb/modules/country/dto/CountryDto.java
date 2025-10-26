package com.movieweb.movieweb.modules.country.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountryDto {
    @NotBlank(message = "Tên quốc gia không được để trống")
    private String name;
}