package com.jimmy.common.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ApplicationException extends BaseException {

    public static final String DEFAULT_ERROR_CODE = "errors.com.jimmy.unknown";

    public static final int DEFAULT_NUMERIC_ERROR_CODE = -1;

    protected int numericErrorCode = DEFAULT_NUMERIC_ERROR_CODE;

    protected String errorCode = DEFAULT_ERROR_CODE;

    protected int httpStatusCode = 500;

    protected ApplicationException(Exception e) {
        super(e);
    }

    protected ApplicationException(
            int httpStatusCode, int numericErrorCode, String errorCode, String pattern, Object... args) {
        super(pattern, args);
        this.httpStatusCode = httpStatusCode;
        this.numericErrorCode = numericErrorCode;
        this.errorCode = errorCode != null ? errorCode : DEFAULT_ERROR_CODE;
    }
}

