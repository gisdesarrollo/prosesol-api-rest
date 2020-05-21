package com.prosesol.api.rest.models.entity;

import com.prosesol.api.rest.models.rel.RelPreguntaRespuestaCandidato;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Luis Enrique Morales Soriano
 */
@Entity
@Table(name = "respuestas")
public class Respuesta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_respuesta")
    private Long id;

    @Column(name = "respuesta")
    private String respuesta;

    @OneToMany(mappedBy = "respuesta", fetch = FetchType.LAZY, cascade = CascadeType.ALL,
                orphanRemoval = true)
    private List<RelPreguntaRespuestaCandidato> relPreguntaRespuestaCandidato;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "respuestas")
    private Set<Pregunta> preguntas;

    public Respuesta(){}

    public Respuesta(Long id, String respuesta) {
        this.id = id;
        this.respuesta = respuesta;
        this.relPreguntaRespuestaCandidato = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public List<RelPreguntaRespuestaCandidato> getRelPreguntaRespuestaCandidato() {
        return relPreguntaRespuestaCandidato;
    }

    public void setRelPreguntaRespuestaCandidato(List<RelPreguntaRespuestaCandidato> relPreguntaRespuestaCandidato) {
        this.relPreguntaRespuestaCandidato = relPreguntaRespuestaCandidato;
    }

    public Set<Pregunta> getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(Set<Pregunta> preguntas) {
        this.preguntas = preguntas;
    }
}
