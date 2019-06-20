package com.prosesol.api.rest.utils;

public class ErrorDetails {

	private String estatus;
	private int code;
	private String message;
	
	public ErrorDetails() {}
	
	public ErrorDetails(String estatus, int code, String message) {
		this.estatus = estatus;
		this.code = code;
		this.message = message;
	}
	
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
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
