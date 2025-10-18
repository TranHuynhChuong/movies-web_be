package com.movieweb.movieweb.common.exception;

public class ForbiddenException extends HttpException {
    private static final String DEFAULT_MESSAGE = "Không có quyền truy cập";

    public ForbiddenException() {
        super(403, DEFAULT_MESSAGE);
    }

    public ForbiddenException(String msg) {
        super(403, msg);
    }
}
