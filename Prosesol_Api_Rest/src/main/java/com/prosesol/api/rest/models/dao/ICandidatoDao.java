package com.prosesol.api.rest.models.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Candidato;

public interface ICandidatoDao extends CrudRepository<Candidato, Long> {

	@Query("select c from Candidato c where c.rfc = ?1")
	public Candidato getCandidatoByRfc(@Param("rfc") String rfc);
	
	@Modifying
	@Query(value="delete from rel_candidatos_pagos where id_candidato= ?1",nativeQuery = true)
	public  void deleteRelCandidatoPagosById(Long idCandidato);

}
