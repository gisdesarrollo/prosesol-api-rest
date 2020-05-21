package com.prosesol.api.rest.models.dao;

import com.prosesol.api.rest.models.rel.RelPreguntaRespuestaCandidato;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IRelPreguntaRespuestaCandidatoDao extends CrudRepository<RelPreguntaRespuestaCandidato, Long> {
}
