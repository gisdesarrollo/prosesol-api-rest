package com.prosesol.api.rest.controllers.exception;

public class PagoException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private int code;

    private String message;

    public PagoException(String message){
        super(message);
    }

    public PagoException(int code, String message){
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

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
