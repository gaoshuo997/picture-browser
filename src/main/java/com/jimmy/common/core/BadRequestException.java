package com.jimmy.common.core;

public class BadRequestException extends ApplicationException {

    public static final int HTTP_STATUS_CODE = 200;

    public static final String DEFAULT_ERROR_CODE = "errors.com.jimmy.bad_request";

    /**
     * constructor.
     *
     * @param numericErrorCode numeric error code.
     * @param errorCode error code.
     * @param pattern message pattern.
     * @param args args.
     */
    public BadRequestException(
            int numericErrorCode, String errorCode, String pattern, Object... args) {
        super(
                HTTP_STATUS_CODE,
                numericErrorCode,
                errorCode != null ? errorCode : DEFAULT_ERROR_CODE,
                pattern,
                args);
    }
}

