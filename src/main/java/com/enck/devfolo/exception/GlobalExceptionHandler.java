package com.enck.devfolo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;

//@ControllerAdvice
@RestControllerAdvice
@Component
//extend not needed
public class GlobalExceptionHandler extends ResponseStatusExceptionHandler {

    ///when i user is not found
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFound.class)
    public ApiErrorInfo userNotFound(Exception ex) {
        return new ApiErrorInfo(ex , ResponseEntity.notFound().build());
    }

    //this is for the proejct
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProjectException.class)
    public ApiErrorInfo ProjectIdNotFound (Exception ex){
        return new ApiErrorInfo(ex , ResponseEntity.notFound().build());
    }


}
