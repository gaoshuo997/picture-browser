package com.jimmy.common.result;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常: {} - URI: {}", e.getMessage(), request.getRequestURI());
        return Result.failed(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@Valid @RequestBody）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Map<String, String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("参数校验失败: {} - URI: {}", e.getMessage(), request.getRequestURI());

//        Map<String, String> errors = new LinkedHashMap<>();
        List<String> errorMessage = new ArrayList<>(e.getBindingResult().getFieldErrors().size());
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
//            errors.put(error.getField(), error.getDefaultMessage());
            errorMessage.add(error.getDefaultMessage());
        }

        return Result.failed(ResultCode.VALIDATE_FAILED, "参数校验失败："+String.join(", ", errorMessage));
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Map<String, String>> handleBindException(
            BindException e, HttpServletRequest request) {
        log.warn("参数绑定失败: {} - URI: {}", e.getMessage(), request.getRequestURI());

        List<String> errorMessage = new ArrayList<>(e.getBindingResult().getFieldErrors().size());
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
//            errors.put(error.getField(), error.getDefaultMessage());
            errorMessage.add(error.getDefaultMessage());
        }

        return Result.failed(ResultCode.VALIDATE_FAILED, "参数绑定失败："+String.join(", ", errorMessage));
    }

    /**
     * 处理约束 violation 异常（@Validated @RequestParam/@PathVariable）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Map<String, String>> handleConstraintViolationException(
            ConstraintViolationException e, HttpServletRequest request) {
        log.warn("约束校验失败: {} - URI: {}", e.getMessage(), request.getRequestURI());

        Map<String, String> errors = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            String fieldName = getFieldName(violation.getPropertyPath());
            errors.put(fieldName, violation.getMessage());
        }

        return Result.failed(ResultCode.VALIDATE_FAILED, "约束校验失败", errors);
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("缺少请求参数: {} - URI: {}", e.getMessage(), request.getRequestURI());
        return Result.failed(ResultCode.VALIDATE_FAILED,
                String.format("缺少必填参数: %s", e.getParameterName()));
    }

    /**
     * 处理缺少路径变量异常
     */
    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMissingPathVariableException(
            MissingPathVariableException e, HttpServletRequest request) {
        log.warn("缺少路径变量: {} - URI: {}", e.getMessage(), request.getRequestURI());
        return Result.failed(ResultCode.VALIDATE_FAILED,
                String.format("缺少路径变量: %s", e.getVariableName()));
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("参数类型不匹配: {} - URI: {}", e.getMessage(), request.getRequestURI());

        String expectedType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown";
        String message = String.format("参数 '%s' 类型错误，期望类型: %s", e.getName(), expectedType);

        return Result.failed(ResultCode.VALIDATE_FAILED, message);
    }

    /**
     * 处理请求消息不可读异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("请求消息解析失败: {} - URI: {}", e.getMessage(), request.getRequestURI());
        return Result.failed(ResultCode.VALIDATE_FAILED, "请求参数格式错误");
    }

    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<?> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("请求方法不支持: {} - URI: {}", e.getMethod(), request.getRequestURI());
        return Result.failed(ResultCode.METHOD_NOT_ALLOWED,
                String.format("不支持请求方法: %s", e.getMethod()));
    }

    /**
     * 处理媒体类型不支持异常
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public Result<?> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException e, HttpServletRequest request) {
        log.warn("媒体类型不支持: {} - URI: {}", e.getMessage(), request.getRequestURI());
        return Result.failed(ResultCode.UNSUPPORTED_MEDIA_TYPE,
                String.format("不支持媒体类型: %s", e.getContentType()));
    }

    /**
     * 处理文件上传大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public Result<?> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException e, HttpServletRequest request) {
        log.warn("文件上传大小超限: {} - URI: {}", e.getMessage(), request.getRequestURI());
        return Result.failed(ResultCode.REQUEST_ENTITY_TOO_LARGE, "文件大小超过限制");
    }

    /**
     * 处理 404 异常
     */
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handleNotFoundException(Exception e, HttpServletRequest request) {
        log.warn("资源不存在: {} - URI: {}", e.getMessage(), request.getRequestURI());
        return Result.failed(ResultCode.NOT_FOUND, "请求的资源不存在");
    }

    /**
     * 处理其他未捕获异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: {} - URI: {}", e.getMessage(), request.getRequestURI(), e);
        return Result.failed(ResultCode.INTERNAL_SERVER_ERROR, "系统内部错误");
    }

    /**
     * 从 PropertyPath 中提取字段名
     */
    private String getFieldName(Path path) {
        if (path == null) {
            return "unknown";
        }
        String fieldName = path.toString();
        if (fieldName.contains(".")) {
            fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
        }
        return fieldName;
    }
}
