package com.prosesol.api.rest.controllers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AfiliadoException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public AfiliadoException(String message) {
		super(message);
	}
	
}
