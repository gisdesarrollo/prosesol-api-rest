package com.prosesol.api.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ErrorPagesJson implements ErrorController {

	@Autowired
	private ErrorAttributes errorAttributes;

	@RequestMapping(value = "/error", method = RequestMethod.GET)
	public Map<String, Object> handleError(HttpServletRequest request, WebRequest webRequest) {

		Map<String, Object> mapErrors = new HashMap<String, Object>();

		String headerAccept = request.getHeader("accept");
		String headerType = request.getHeader("content-type");

		if (headerAccept.equals("*/*") || headerType.contains("application/json")
				|| !headerAccept.contains("text/html")) {
			mapErrors = errorAttributes.getErrorAttributes(webRequest, true);
		}
		return mapErrors;
	}

	@Override
	public String getErrorPath() {

		return "/error";
	}
}
