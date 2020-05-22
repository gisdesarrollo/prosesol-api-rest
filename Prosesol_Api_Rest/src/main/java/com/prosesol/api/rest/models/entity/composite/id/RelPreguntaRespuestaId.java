package com.prosesol.api.rest.models.entity.composite.id;

import java.io.Serializable;

/**
 * @author Luis Enrique Morales Soriano
 */
public class RelPreguntaRespuestaId implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Long pregunta;

    private Long respuesta;

    public RelPreguntaRespuestaId() {
    }

    public RelPreguntaRespuestaId(Long pregunta, Long respuesta) {
        this.pregunta = pregunta;
        this.respuesta = respuesta;
    }

    public Long getPregunta() {
        return pregunta;
    }

    public void setPregunta(Long pregunta) {
        this.pregunta = pregunta;
    }

    public Long getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(Long respuesta) {
        this.respuesta = respuesta;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pregunta == null) ? 0 : pregunta.hashCode());
        result = prime * result + ((respuesta == null) ? 0 : respuesta.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RelPreguntaRespuestaId other = (RelPreguntaRespuestaId) obj;
        if (pregunta == null) {
            if (other.pregunta != null)
                return false;
        } else if (!pregunta.equals(other.pregunta))
            return false;
        if (respuesta == null) {
            if (other.respuesta != null)
                return false;
        } else if (!respuesta.equals(other.respuesta))
            return false;
        return true;
    }
}
