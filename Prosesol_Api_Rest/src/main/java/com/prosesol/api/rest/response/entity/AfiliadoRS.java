package com.prosesol.api.rest.response.entity;

import com.prosesol.api.rest.models.entity.Afiliado;

public class AfiliadoRS {

	private Afiliado afiliado;
	
	private String estatus;
	
	private int code;
	
	private String mensaje;
	
	public AfiliadoRS() {}
	
	public AfiliadoRS(Afiliado afiliado, String estatus, int code, String mensaje) {
		this.afiliado = afiliado;
		this.estatus = estatus;
		this.code = code;
		this.mensaje = mensaje;
	}
	
	public Afiliado getAfiliado() {
		return afiliado;
	}

	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
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

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
	@Override
	public String toString() {

		final StringBuilder builder = new StringBuilder();
		
		if(afiliado != null) {
			builder.append("Afiliado: ").append(afiliado.toString()).append("\n")
			   .append("estatus: ").append(estatus).append("\n")
			   .append("code: ").append(code).append("\n")
			   .append("message: ").append(mensaje);
		}else {
			builder.append("estatus: ").append(estatus).append("\n")
			   .append("code: ").append(code).append("\n")
			   .append("message: ").append(mensaje);
		}
		
		
		
		return builder.toString();
		
	}


	
}
