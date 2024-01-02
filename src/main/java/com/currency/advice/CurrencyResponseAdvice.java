package com.currency.advice;

import com.currency.exception.DataNotFoundException;
import com.currency.exception.ParseException;
import com.currency.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.FieldError;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Collections;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class CurrencyResponseAdvice implements ResponseBodyAdvice<Object> {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("[MethodArgumentNotValidException] errMsg: {}", e.getMessage());
        return new ApiResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), errMsg, Collections.emptyMap());
    }

    @ExceptionHandler(ParseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse handleJsonProcessingException(ParseException e) {
        log.error("[ParseException] errMsg: {}", e.getMessage());
        return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse handleDuplicateKeyException(DuplicateKeyException e) {
        log.error("[DuplicateKeyException] errMsg: {}", e.getMessage());
        return new ApiResponse(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), e.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse handleDataNotFoundException(DataNotFoundException e) {
        log.error("[DataNotFoundException] errMsg: {}", e.getMessage());
        return new ApiResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("[NoHandlerFoundException] errMsg: {}", e.getMessage());
        return new ApiResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse handleException(Exception e) {
        log.error("[Exception] errMsg: {}", e.getMessage());
        return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage(), Collections.emptyMap());
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) { return true; }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {


        if(body instanceof ApiResponse ) {
            return body;
        }else {
            if (body == null) {
                body = Collections.emptyMap();
            }
            return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "SUCCESS", body);
        }
    }
}
