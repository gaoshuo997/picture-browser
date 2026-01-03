package com.jimmy.common.core;

public class UnauthorizedException extends ApplicationException {

    public static final String DEFAULT_ERROR_CODE = "errors.com.jimmy.unauthorized";

    public static final int HTTP_STATUS_CODE = 200;

    /**
     * constructor.
     *
     * @param numericErrorCode numeric error code.
     * @param errorCode error code.
     * @param pattern message pattern.
     * @param args args.
     */
    public UnauthorizedException(
            int numericErrorCode, String errorCode, String pattern, Object... args) {
        super(
                HTTP_STATUS_CODE,
                numericErrorCode,
                errorCode != null ? errorCode : DEFAULT_ERROR_CODE,
                pattern,
                args);
    }
}

