package com.prosesol.api.rest.models.entity.composite.id;

import java.io.Serializable;

/**
 * @author Luis Enrique Morales Soriano
 */
public class RelPreguntaRespuestaCandidatoId implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Long pregunta;

    private Long respuesta;

    private Long candidato;

    public RelPreguntaRespuestaCandidatoId(){}

    public RelPreguntaRespuestaCandidatoId(Long pregunta, Long respuesta, Long candidato) {
        this.pregunta = pregunta;
        this.respuesta = respuesta;
        this.candidato = candidato;
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

    public Long getCandidato() {
        return candidato;
    }

    public void setCandidato(Long candidato) {
        this.candidato = candidato;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pregunta == null) ? 0 : pregunta.hashCode());
        result = prime * result + ((respuesta == null) ? 0 : respuesta.hashCode());
        result = prime * result + ((candidato == null) ? 0 : candidato.hashCode());
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
        RelPreguntaRespuestaCandidatoId other = (RelPreguntaRespuestaCandidatoId) obj;
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
        if (candidato == null) {
            if (other.candidato != null)
                return false;
        } else if (!candidato.equals(other.candidato))
            return false;
        return true;
    }
}
