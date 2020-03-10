package com.prosesol.api.rest.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorPages {
//	 @RequestMapping(value="/error", method = RequestMethod.GET)
//	 public String handleError(HttpServletRequest request,Model model) {
//
//	        String errorMsg = "";
//	        int httpErrorCode = (int) request.getAttribute("javax.servlet.error.status_code");
//	        Object httpErrorMessage =request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
//	        int errorCodeMsg=0;
//	        switch (httpErrorCode) {
//	            case 400: {
//	                errorMsg = "Http Error Code: 400. Bad Request";
//	                errorCodeMsg=httpErrorCode;
//
//	                break;
//	            }
//	            case 401: {
//	                errorMsg = "Http Error Code: 401. Unauthorized";
//	                errorCodeMsg=httpErrorCode;
//	                break;
//	            }
//	            case 403: {
//	                errorMsg = "Http Error Code: 403. Access Denied";
//	                errorCodeMsg=httpErrorCode;
//	                break;
//	            }
//	            case 404: {
//	                errorMsg = "Http Error Code: 404. Resource not found";
//	                errorCodeMsg=httpErrorCode;
//	                break;
//	            }
//	            case 500: {
//	                errorMsg = "Http Error Code: 500. Internal Server Error";
//	                errorCodeMsg=httpErrorCode;
//	                break;
//	            }
//	            default:{
//	            	errorMsg = "Http Error Code: "+httpErrorCode+". "+ httpErrorMessage;
//	                errorCodeMsg=httpErrorCode;
//	                break;
//	            }
//	        }
//	        model.addAttribute("errorCode", errorCodeMsg);
//	        model.addAttribute("error", errorMsg);
//	        return "error/errorPage";
//	    }
//
//
//
//	@Override
//	public String getErrorPath() {
//		 return "/error";
//	}
	
}
