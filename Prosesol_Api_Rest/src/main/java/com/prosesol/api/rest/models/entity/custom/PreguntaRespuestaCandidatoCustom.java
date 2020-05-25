package com.prosesol.api.rest.models.entity.custom;

/*
 * @autor Alexander Garcia Martinez
 *
 * */
public class PreguntaRespuestaCandidatoCustom {
	
	private String pregunta;
	
	private String respuesta;

	public PreguntaRespuestaCandidatoCustom(String pregunta, String respuesta) {
		
		this.pregunta = pregunta;
		this.respuesta = respuesta;
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

	@Override
	public String toString() {
		return "PreguntaRespuestaCandidatoCustom [pregunta=" + pregunta + ", respuesta=" + respuesta + "]";
	}
	
}
