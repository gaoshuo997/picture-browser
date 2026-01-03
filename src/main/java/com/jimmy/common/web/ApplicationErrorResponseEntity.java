package com.jimmy.common.web;

import lombok.Data;

@Data
public class ApplicationErrorResponseEntity {

    private int numericErrorCode;

    private String errorCode;

    private String message;
}