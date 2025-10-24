package com.movieweb.movieweb.modules.genre.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenreDto {
    @NotBlank(message = "Tên thể loại không được để trống")
    private String name;
}