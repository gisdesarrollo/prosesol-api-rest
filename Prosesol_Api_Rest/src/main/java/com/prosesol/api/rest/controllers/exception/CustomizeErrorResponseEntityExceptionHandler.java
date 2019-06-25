package com.prosesol.api.rest.controllers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.prosesol.api.rest.utils.ErrorDetails;

@ControllerAdvice
@RestController
public class CustomizeErrorResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(AfiliadoException.class)
	public final ResponseEntity<ErrorDetails> handleAfiliadoException(AfiliadoException ex, 
			WebRequest request) {

		System.out.println(ex.getMessage());
		
		ErrorDetails errorDetails = new ErrorDetails("ERR", ex.getCode(), 
				ex.getMessage());
		
		return new ResponseEntity<>(errorDetails, HttpStatus.valueOf(ex.getCode()));
		
	}

	@ExceptionHandler(OpenpayException.class)
	public final ResponseEntity<ErrorDetails> handleAfiliadoException(OpenpayException ex, 
			WebRequest request) {
		
		ErrorDetails errorDetails = new ErrorDetails("ERR", ex.getErrorCode(), 
				ex.getMessage());
		
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
}
