package com.prosesol.api.rest.controllers;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Controller
@ControllerAdvice
public class ErrorPagesHtml {

	private static final Logger logger = LoggerFactory.getLogger(ErrorPagesHtml.class);
	  
    @ExceptionHandler
    public String handleException(Exception ex, HttpServletRequest request, Model model) {
        logger.info("executing exception handler (web)");
        int httpErrorCode = (int) request.getAttribute("javax.servlet.error.status_code");
        Object httpErrorMessage =request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        model.addAttribute("errorCode", httpErrorCode);
        model.addAttribute("error","Http Error Code: "+httpErrorCode+" "+httpErrorMessage);
        model.addAttribute("message", ex.getMessage());
       
  
      
        return "error/errorPage";
    }
}
