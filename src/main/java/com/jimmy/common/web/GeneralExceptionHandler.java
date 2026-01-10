package com.jimmy.common.web;

import com.jimmy.common.core.ApplicationException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GeneralExceptionHandler {

    /**
     * handle application exception.
     *
     * @param exception application exception.
     * @param response http servlet response.
     * @return application error response.
     */
    @ExceptionHandler(ApplicationException.class)
    public ApplicationResponseEntity<ApplicationErrorResponseEntity> handleApplicationException(
            ApplicationException exception, HttpServletResponse response) {
//        response.setStatus(exception.getHttpStatusCode());
//        ApplicationResponseEntity<ApplicationErrorResponseEntity> res =
//                new ApplicationResponseEntity<>();
//        res.setActionStatus(ActionStatus.FAIL);
        return constructValidationErrorResponse(exception.getErrorCode(), exception.getMessage());

//        res.setContent(mapperFacade.map(exception, ApplicationErrorResponseEntity.class));
//        return res;
    }

    /**
     * handle method argument not valid exception.
     *
     * @param exception method argument not valid exception.
     * @return validation error response.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApplicationResponseEntity<ApplicationErrorResponseEntity> handleValidationException(
            MethodArgumentNotValidException exception) {
        List<String> errorMessages = new ArrayList<>();
        exception.getBindingResult().getAllErrors().forEach(
                objectError -> {
                    errorMessages.add(objectError.getDefaultMessage());
                });
        log.error(
                "Method arguments validation failed. errors:{}", errorMessages);

        return constructValidationErrorResponse("MethodArgumentNotValidException",
                errorMessages.get(0));
    }

    /**
     * handle bad request.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApplicationResponseEntity<ApplicationErrorResponseEntity>
    handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.error("UnExpected http message not readable exception: error:{}", exception.getMessage(), exception);

        return constructValidationErrorResponse("HttpMessageNotReadableException",
                exception.getMessage());
    }

    /**
     * handle bad request.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApplicationResponseEntity<ApplicationErrorResponseEntity>
    handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        log.error("UnExpected method argument type mismatch exception: error:{}", exception.getMessage(),
                exception);

        return constructValidationErrorResponse("MethodArgumentTypeMismatchException",
                exception.getMessage());
    }

    /**
     * Handle request/path validation error.
     *
     * @param exception constraint violation exception.
     * @return error map.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApplicationResponseEntity<ApplicationErrorResponseEntity> handle(
            ConstraintViolationException exception) {
        log.error("UnExpected constraint violation exception: error:{}", exception.getMessage(),
                exception);

        Map<String,List<String>> errorMessages = Collections.singletonMap("errors", exception
                .getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList()));

        List<String> errors = errorMessages.get("errors");

        return constructValidationErrorResponse("ConstraintViolationException",
                errors.get(0));
    }

    /**
     * handle bad request.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApplicationResponseEntity<ApplicationErrorResponseEntity>
    handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exception) {
        log.error("UnExpected method argument type mismatch exception: error:{}", exception.getMessage(),
                exception);

        return constructValidationErrorResponse("MaxUploadSizeExceededException",
                "文件大小超出最大限制");
    }

    /**
     * handle bad request missing require parameter.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApplicationResponseEntity<ApplicationErrorResponseEntity>
    handleMaxUploadSizeExceededException(MissingServletRequestParameterException exception) {
        log.error("Missing required parameter exception: error:{}", exception.getMessage(),
                exception);

        return constructValidationErrorResponse("MissingServletRequestParameterException",
                String.format("缺少必输的参数：%s", exception.getParameterName()));
    }

    /**
     * Handle unExcepted runtime error.
     *
     * @param exception constraint violation exception.
     * @return error map.
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApplicationResponseEntity<ApplicationErrorResponseEntity> handleRuntimeException(
            RuntimeException exception) {
        log.error("UnExpected runtime exception: error:{}", exception.getMessage(), exception);

        return constructValidationErrorResponse("RuntimeException", exception.getMessage());
    }

    /**
     * construct validation error response.
     *
     * @param errorCode    errorCode
     * @param errorMessage errorMessage
     * @return ApplicationResponseEntity
     */
    private ApplicationResponseEntity<ApplicationErrorResponseEntity>
    constructValidationErrorResponse(String errorCode,
                                     String errorMessage) {
        ApplicationErrorResponseEntity errorResponseEntity = new ApplicationErrorResponseEntity();
        errorResponseEntity.setNumericErrorCode(-1);
        errorResponseEntity.setErrorCode(errorCode);
        errorResponseEntity.setMessage(errorMessage);

        ApplicationResponseEntity<ApplicationErrorResponseEntity> responseEntity =
                new ApplicationResponseEntity<>();
        responseEntity.setActionStatus(ActionStatus.FAIL);
        responseEntity.setContent(errorResponseEntity);

        return responseEntity;
    }
}

