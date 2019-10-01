package com.prosesol.api.rest.utils;

public class Clave {
	
	public String getClaveAfiliado() {

		String claveAfiliado = "PR-";
		String clave = "0123456789";

		for (int i = 0; i < 10; i++) {
			claveAfiliado += (clave.charAt((int) (Math.random() * clave.length())));
		}

		return claveAfiliado;
	}

}
