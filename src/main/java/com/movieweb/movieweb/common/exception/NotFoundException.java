package com.movieweb.movieweb.common.exception;

public class NotFoundException extends HttpException {
    private static final String DEFAULT_MESSAGE = "Không tìm thấy tài nguyên";

    public NotFoundException() {
        super(404, DEFAULT_MESSAGE);
    }

    public NotFoundException(String msg) {
        super(404, msg);
    }
}
