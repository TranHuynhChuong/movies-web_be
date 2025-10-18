package com.movieweb.movieweb.common.dto;

public class ResponseHelper {

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, "success", message, data);
    }

    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return new ApiResponse<>(statusCode, "error", message, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", "Thành công", data);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "success", "Thành công", null);
    }

    public static <T> ApiResponse<T> error(int statusCode) {
        return new ApiResponse<>(statusCode, "error", "Lỗi!", null);
    }
}