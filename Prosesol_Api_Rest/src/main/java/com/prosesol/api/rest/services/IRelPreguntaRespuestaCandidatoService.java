package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.rel.RelPreguntaRespuestaCandidato;

import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IRelPreguntaRespuestaCandidatoService{

    public void save(RelPreguntaRespuestaCandidato relPreguntaRespuestaCandidato);

    public List<RelPreguntaRespuestaCandidato> findAll();

    public RelPreguntaRespuestaCandidato findById(Long id);
}
