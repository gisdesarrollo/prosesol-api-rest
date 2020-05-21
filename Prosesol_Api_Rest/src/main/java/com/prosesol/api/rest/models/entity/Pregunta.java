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
@Table(name = "preguntas")
@Entity
public class Pregunta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pregunta")
    private Long id;

    @Column(name = "pregunta")
    private String pregunta;

    @OneToMany(mappedBy = "pregunta", fetch = FetchType.LAZY,
                cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RelPreguntaRespuestaCandidato> relPreguntaRespuestaCandidato;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL, CascadeType.MERGE})
    @JoinTable(name = "rel_preguntas_respuestas", joinColumns = @JoinColumn(name = "id_pregunta"),
                inverseJoinColumns = @JoinColumn(name = "id_respuesta"))
    private Set<Respuesta> respuestas;

    public Pregunta(){}

    public Pregunta(Long id, String pregunta) {
        this.id = id;
        this.pregunta = pregunta;
        this.relPreguntaRespuestaCandidato = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public List<RelPreguntaRespuestaCandidato> getRelPreguntaRespuestaCandidato() {
        return relPreguntaRespuestaCandidato;
    }

    public void setRelPreguntaRespuestaCandidato(List<RelPreguntaRespuestaCandidato> relPreguntaRespuestaCandidato) {
        this.relPreguntaRespuestaCandidato = relPreguntaRespuestaCandidato;
    }

    public Set<Respuesta> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(Set<Respuesta> respuestas) {
        this.respuestas = respuestas;
    }
}
