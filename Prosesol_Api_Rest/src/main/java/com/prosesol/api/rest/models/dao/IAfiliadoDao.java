package com.prosesol.api.rest.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.prosesol.api.rest.models.entity.Afiliado;

public interface IAfiliadoDao extends CrudRepository<Afiliado, Long> {

	@Query("select a from Afiliado a where a.rfc = ?1")
	public Afiliado getAfiliadoByRfc(@Param("rfc") String rfc);
	
	@Query(value = "select a.* from afiliados a, rel_afiliados_beneficiarios b where a.id_afiliado = b.id_beneficiario and  a.is_beneficiario = true and b.id_afiliado = ?1", nativeQuery = true)
	public List<Afiliado> getBeneficiarioByIdByIsBeneficiario(Long id);

}
