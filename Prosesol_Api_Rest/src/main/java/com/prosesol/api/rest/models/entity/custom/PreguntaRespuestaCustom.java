package com.prosesol.api.rest.models.entity.custom;

/**
 * @author Luis Enrique Morales Soriano
 */
public class PreguntaRespuestaCustom {

    private Long idPregunta;

    private Long idRespuesta;

    private String pregunta;

    private String respuesta;

    public PreguntaRespuestaCustom(Long idPregunta, Long idRespuesta, String pregunta, String respuesta) {
        this.idPregunta = idPregunta;
        this.idRespuesta = idRespuesta;
        this.pregunta = pregunta;
        this.respuesta = respuesta;
    }

    public Long getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(Long idPregunta) {
        this.idPregunta = idPregunta;
    }

    public Long getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(Long idRespuesta) {
        this.idRespuesta = idRespuesta;
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
        return "PreguntaRespuestaCustom{" +
                "idPregunta=" + idPregunta +
                ", idRespuesta=" + idRespuesta +
                ", pregunta='" + pregunta + '\'' +
                ", respuesta='" + respuesta + '\'' +
                '}';
    }
}
