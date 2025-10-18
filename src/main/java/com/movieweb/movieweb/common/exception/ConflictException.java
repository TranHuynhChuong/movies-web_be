package com.movieweb.movieweb.common.exception;

public class ConflictException extends HttpException {
    private static final String DEFAULT_MESSAGE = "Tài nguyên đã tồn tại";

    public ConflictException() {
        super(409, DEFAULT_MESSAGE);
    }

    public ConflictException(String msg) {
        super(409, msg);
    }
}
