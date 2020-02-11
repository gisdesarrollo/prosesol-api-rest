package com.prosesol.api.rest.controllers.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;

/**
 * @author Luis Enrique Morales Soriano
 */
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AfiliadoException.class)
    protected ResponseEntity<Object> handleInvalidRequest(RuntimeException re, WebRequest request){

        AfiliadoException afiliadoException = (AfiliadoException) re;
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();

        response.put("estatus", "ERR");
        response.put("code", HttpStatus.BAD_REQUEST.value());
        response.put("message", afiliadoException.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(re, response, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex
            , HttpHeaders headers, HttpStatus status, WebRequest request) {

        Throwable throwable = ex.getCause();
        String message = null;
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();

        if(throwable instanceof JsonMappingException){
            if(throwable.getCause() instanceof AfiliadoException){
                return handleInvalidRequest((RuntimeException) throwable.getCause(), request);
            }else{
                message = throwable.getMessage();
            }
        }else{
            message = throwable.getMessage();
        }

        response.put("estatus", "ERR");
        response.put("code", HttpStatus.BAD_REQUEST.value());
        response.put("message", message);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(ex, response, headers, HttpStatus.BAD_REQUEST, request);
    }
}
