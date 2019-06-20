package com.prosesol.api.rest.controllers.exception;

import mx.openpay.client.exceptions.OpenpayServiceException;

public class OpenpayException extends OpenpayServiceException{

	private static final long serialVersionUID = 1L;
	
	private int code;
	private String message;

	public OpenpayException(String message) {
		super(message);
	}

	public OpenpayException(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
