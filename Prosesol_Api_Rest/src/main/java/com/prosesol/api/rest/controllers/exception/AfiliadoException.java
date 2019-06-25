package com.prosesol.api.rest.controllers.exception;

public class AfiliadoException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private int code;
	
	private String message;

	public AfiliadoException(String message) {
		super(message);
	}
	
	public AfiliadoException(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
}
