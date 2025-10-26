package com.movieweb.movieweb.common.exception;

import com.movieweb.movieweb.common.dto.ApiResponse;
import com.movieweb.movieweb.common.dto.ResponseHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpException(HttpException ex) {
        System.out.println(ex.getMessage());
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ResponseHelper.error(ex.getStatusCode(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        System.out.println(ex.getMessage());
        return ResponseEntity
                .status(500)
                .body(ResponseHelper.error(500, "Lỗi hệ thống"));
    }
}
