package com.prosesol.api.rest.models.rel;

import com.prosesol.api.rest.models.entity.Pregunta;
import com.prosesol.api.rest.models.entity.Respuesta;
import com.prosesol.api.rest.models.entity.composite.id.RelPreguntaRespuestaId;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Luis Enrique Morales Soriano
 */
@Entity
@Table(name = "rel_preguntas_respuestas")
@IdClass(RelPreguntaRespuestaId.class)
public class RelPreguntaRespuesta implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_pregunta")
    private Pregunta pregunta;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_respuesta")
    private Respuesta respuesta;

    public Pregunta getPregunta() {
        return pregunta;
    }

    public void setPregunta(Pregunta pregunta) {
        this.pregunta = pregunta;
    }

    public Respuesta getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(Respuesta respuesta) {
        this.respuesta = respuesta;
    }
}
