package com.wechat.cn.exception;

import com.wechat.cn.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : 志敏.罗
 * @create 2022/6/30 14:09
 */
@Slf4j
@RestControllerAdvice
public class MyAdviceException {

    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> verifyErrorHandler(HttpServletRequest request, Exception e) {
        log.error("[exception:GlobalExceptionHandler] {} {}", request.getMethod(), request.getRequestURI());
        ConstraintViolationException ce = (ConstraintViolationException) e;
        Set<ConstraintViolation<?>> constraintViolationSet = ce.getConstraintViolations();
        StringBuilder sb = new StringBuilder();
        if (null != constraintViolationSet && !constraintViolationSet.isEmpty()) {
            for (ConstraintViolation<?> violation : constraintViolationSet) {
                sb.append(violation.getPropertyPath());
                sb.append(":");
                sb.append(violation.getMessage());
                sb.append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        Result<?> result = new Result<>();
        result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        //system error
        result.setMessage(sb.toString());
        log.error("[exception:GlobalExceptionHandler] parameter verify error, error message: {}", sb);
        return result;
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> verifyErrorHandlers(HttpServletRequest request, Exception e) {
        log.error("[exception:GlobalExceptionHandler] {} {}", request.getMethod(), request.getRequestURI());
        MethodArgumentNotValidException ce = (MethodArgumentNotValidException) e;
        BindingResult bindingResult = ce.getBindingResult();
        StringBuilder sb = new StringBuilder();
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrorList) {
                sb.append(fieldError.getDefaultMessage());
                sb.append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        Result<?> result = new Result<>();
        result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        //system error
        result.setMessage(sb.toString());
        log.error("[exception:GlobalExceptionHandler] parameter verify error, error message: {}", sb);
        return result;
    }

    @ExceptionHandler(value = ServiceException.class)
    @ResponseBody
    public Result<ServiceException> handlerServiceException(HttpServletRequest request, Exception e) {
        log.error("[exception:ServiceException] {} {}", request.getMethod(), request.getRequestURI());
        ServiceException exception = (ServiceException) e;
        Result<ServiceException> result = new Result<>();
        result.setCode(exception.getCode());
        result.setMessage(exception.getMessage());
        log.error("[exception:ServiceException] controller class raise exception ", e);
        return result;
    }


    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result<?> handler(HttpServletRequest request, Exception e) {
        log.error("[exception:GlobalExceptionHandler] {} {}", request.getMethod(), request.getRequestURI());
        Result<?> result = new Result<>();
        result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        result.setMessage("系统异常，请稍后重试");
        log.error("[exception:GlobalExceptionHandler] controller class raise exception ", e);
        return result;
    }
}
