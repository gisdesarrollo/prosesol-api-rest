package com.prosesol.api.rest.utils;

import org.springframework.stereotype.Service;

@Service
public class GenerarClave {

	private String clave;

	public String getClave() {

		this.clave = "PR-";
		String num="0123456789";
		for (int i = 0; i < 10; i++) {
			this.clave += (num.charAt((int) (Math.random() * num.length())));
		}

		return this.clave;
	}

}
