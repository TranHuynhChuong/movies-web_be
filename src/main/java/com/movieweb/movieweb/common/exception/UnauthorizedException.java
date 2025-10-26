package com.movieweb.movieweb.common.exception;

public class UnauthorizedException extends HttpException {
    private static final String DEFAULT_MESSAGE = "Yêu cầu đăng nhập";

    public UnauthorizedException() {
        super(401, DEFAULT_MESSAGE);
    }

    public UnauthorizedException(String msg) {
        super(401, msg);
    }
}
