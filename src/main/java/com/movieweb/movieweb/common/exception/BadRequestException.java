package com.movieweb.movieweb.common.exception;

public class BadRequestException extends HttpException {
    private static final String DEFAULT_MESSAGE = "Yêu cầu không hợp lệ";

    public BadRequestException() {
        super(400, DEFAULT_MESSAGE);
    }

    public BadRequestException(String msg) {
        super(400, msg);
    }
}
