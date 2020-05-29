package com.prosesol.api.rest.models.rel;

import com.prosesol.api.rest.models.entity.Candidato;
import com.prosesol.api.rest.models.entity.Pregunta;
import com.prosesol.api.rest.models.entity.Respuesta;
import com.prosesol.api.rest.models.entity.composite.id.RelPreguntaRespuestaCandidatoId;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Luis Enrique Morales Soriano
 */
@Entity
@Table(name = "rel_preguntas_respuestas_candidatos")
@IdClass(RelPreguntaRespuestaCandidatoId.class)
public class RelPreguntaRespuestaCandidato implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_candidato")
    private Candidato candidato;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pregunta")
    private Pregunta pregunta;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_respuesta")
    private Respuesta respuesta;
    
    @Column(name="fecha_registro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    public RelPreguntaRespuestaCandidato() {
    }

    public RelPreguntaRespuestaCandidato(Candidato candidato, Pregunta pregunta, Respuesta respuesta, Date fecha) {
        this.candidato = candidato;
        this.pregunta = pregunta;
        this.respuesta = respuesta;
        this.fecha = fecha;
    }

    public Candidato getCandidato() {
        return candidato;
    }

    public void setCandidato(Candidato candidato) {
        this.candidato = candidato;
    }

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

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
    
    
}
