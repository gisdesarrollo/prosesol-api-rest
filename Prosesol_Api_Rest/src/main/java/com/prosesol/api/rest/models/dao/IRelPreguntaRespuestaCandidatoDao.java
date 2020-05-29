package com.prosesol.api.rest.models.dao;

import com.prosesol.api.rest.models.entity.custom.PreguntaRespuestaCandidatoCustom;
import com.prosesol.api.rest.models.rel.RelPreguntaRespuestaCandidato;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IRelPreguntaRespuestaCandidatoDao extends CrudRepository<RelPreguntaRespuestaCandidato, Long> {
	 @Query("select new com.prosesol.api.rest.models.entity.custom.PreguntaRespuestaCandidatoCustom(" +
	            "p.pregunta, r.respuesta) from RelPreguntaRespuestaCandidato rel " +
	            "join rel.pregunta p " +
	            "join rel.respuesta r " +
	            "where rel.pregunta.id = p.id and rel.respuesta.id = r.id and rel.candidato.id=?1 ")
	    public List<PreguntaRespuestaCandidatoCustom> getPreguntaAndRespuestaBycandidatoById(@Param("id") Long id);
	 
	@Query("select new com.prosesol.api.rest.models.entity.custom.PreguntaRespuestaCandidatoCustom(" +
	            "rel.candidato.id,rel.fecha) from RelPreguntaRespuestaCandidato rel " +
	            "group by rel.candidato.id")
	 public List<PreguntaRespuestaCandidatoCustom> getDatetimeByCandidato();
}
