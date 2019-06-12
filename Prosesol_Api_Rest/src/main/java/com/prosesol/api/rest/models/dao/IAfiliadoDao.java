package com.prosesol.api.rest.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.prosesol.api.rest.models.entity.Afiliado;

public interface IAfiliadoDao extends CrudRepository<Afiliado, Long>{

	@Query("select a from Afiliado a where a.rfc = ?1")
	public Afiliado getAfiliadoByRfc(@Param("rfc")String rfc);
	
}
