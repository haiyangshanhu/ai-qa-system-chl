package com.ai.qa.user.api.exception;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理器
 * 统一处理控制器层抛出的各种异常，返回规范的错误响应格式
 *
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常（BusinessException）
     * 捕获所有业务逻辑相关的异常，如用户不存在、密码错误等
     *
     * @param ex      业务异常对象
     * @param request HTTP请求对象，用于获取请求信息
     * @return ResponseEntity 包含错误信息的响应实体
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex,
            HttpServletRequest request) {
        // 记录警告日志，包含错误代码、消息和请求路径
        log.warn("业务异常: 错误代码={}, 错误消息={}, 请求路径={}",
                ex.getErrorCode(), ex.getErrorMessage(), request.getRequestURI());

        // 构建错误响应
        Map<String, Object> errorResponse = buildErrorResponse(ex.getErrorCode(), ex.getErrorMessage());

        // 根据错误代码确定HTTP状态码
        HttpStatus status = determineHttpStatus(ex.getErrorCode());

        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * 处理参数校验异常（MethodArgumentNotValidException）
     * 捕获Spring Validation框架的参数校验失败异常
     *
     * @param ex      参数校验异常对象
     * @param request HTTP请求对象
     * @return ResponseEntity 包含详细校验错误信息的响应实体
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        // 记录警告日志
        log.warn("参数校验异常: {}, 请求路径: {}", ex.getMessage(), request.getRequestURI());

        // 收集所有字段级别的错误信息
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        // 构建基础错误响应
        Map<String, Object> errorResponse = buildErrorResponse(ErrCode.BAD_REQUEST, ErrCode.MSG_BAD_REQUEST);
        // 添加详细的字段错误信息
        errorResponse.put("details", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 处理其他所有未捕获的异常（Exception）
     * 作为兜底处理，捕获所有未被特定处理的异常
     *
     * @param ex      异常对象
     * @param request HTTP请求对象
     * @return ResponseEntity 包含内部错误信息的响应实体
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, HttpServletRequest request) {
        // 记录错误日志，包含堆栈信息
        log.error("系统异常: 异常消息={}, 请求路径={}", ex.getMessage(), request.getRequestURI(), ex);

        // 构建通用的内部错误响应
        Map<String, Object> errorResponse = buildErrorResponse(
                ErrCode.INTERNAL_SERVER_ERROR,
                ErrCode.MSG_INTERNAL_ERROR);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFound(UsernameNotFoundException ex) {
        Map<String, Object> error = buildErrorResponse(ErrCode.USER_NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // @ExceptionHandler(InvalidJwtException.class)
    // public ResponseEntity<Map<String, Object>>
    // handleInvalidJwt(InvalidJwtException ex) {
    // Map<String, Object> error = buildErrorResponse(ErrCode.INVALID_TOKEN,
    // ErrCode.MSG_INVALID_TOKEN);
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    // }

    /**
     * 构建统一的错误响应格式
     *
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     * @return Map<String, Object> 错误响应数据
     */
    private Map<String, Object> buildErrorResponse(Integer errorCode, String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("errorCode", errorCode);
        response.put("errorMessage", errorMessage);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    /**
     * 根据业务错误代码确定对应的HTTP状态码
     * 将业务错误代码映射到合适的HTTP状态码
     *
     * @param errorCode 业务错误代码
     * @return HttpStatus 对应的HTTP状态码
     */
    private HttpStatus determineHttpStatus(Integer errorCode) {
        if (errorCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        // 根据错误代码返回相应的HTTP状态码
        return switch (errorCode) {
            case ErrCode.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case ErrCode.FORBIDDEN -> HttpStatus.FORBIDDEN;
            case ErrCode.NOT_FOUND -> HttpStatus.NOT_FOUND;
            case ErrCode.CONFLICT -> HttpStatus.CONFLICT;
            case ErrCode.USER_NOT_FOUND,
                    ErrCode.USER_ALREADY_EXISTS,
                    ErrCode.PASSWORD_INCORRECT,
                    ErrCode.PASSWORD_MISMATCH,
                    ErrCode.INVALID_TOKEN ->
                HttpStatus.BAD_REQUEST;
            case ErrCode.INTERNAL_SERVER_ERROR,
                    ErrCode.SERVICE_UNAVAILABLE ->
                HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.BAD_REQUEST; // 默认返回400错误
        };
    }
}