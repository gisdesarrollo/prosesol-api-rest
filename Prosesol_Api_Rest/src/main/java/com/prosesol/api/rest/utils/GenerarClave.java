package com.prosesol.api.rest.utils;

import org.springframework.stereotype.Service;

@Service
public class GenerarClave {

	private String clave;

	public String getClaveAfiliado(String clave) {

		this.clave = "PR-";

		for (int i = 0; i < 10; i++) {
			this.clave += (clave.charAt((int) (Math.random() * clave.length())));
		}

		return this.clave;
	}

}
