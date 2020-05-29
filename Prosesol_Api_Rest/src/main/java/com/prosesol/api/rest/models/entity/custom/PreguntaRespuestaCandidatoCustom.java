package com.prosesol.api.rest.models.entity.custom;

import java.util.Date;

/*
 * @autor Alexander Garcia Martinez
 *
 * */
public class PreguntaRespuestaCandidatoCustom {

	private String pregunta;

	private String respuesta;

	private Long idAfiliado;

	private Date fecha;

	public PreguntaRespuestaCandidatoCustom() {}

	public PreguntaRespuestaCandidatoCustom(String pregunta, String respuesta) {
		this.pregunta = pregunta;
		this.respuesta = respuesta;
	}

	public PreguntaRespuestaCandidatoCustom(Long idAfiliado, Date fecha) {
		this.idAfiliado = idAfiliado;
		this.fecha = fecha;
	}

	public String getPregunta() {
		return pregunta;
	}

	public void setPregunta(String pregunta) {
		this.pregunta = pregunta;
	}

	public String getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}

	public Long getIdAfiliado() {
		return idAfiliado;
	}

	public void setIdAfiliado(Long idAfiliado) {
		this.idAfiliado = idAfiliado;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

}
