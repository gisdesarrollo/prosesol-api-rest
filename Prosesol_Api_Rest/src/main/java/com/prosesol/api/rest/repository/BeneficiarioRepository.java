package com.prosesol.api.rest.repository;

import com.prosesol.api.rest.models.entity.Afiliado;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

@Repository
public class BeneficiarioRepository {

	@PersistenceContext
	public EntityManager entity;
	
	@Transactional
	public void insertBeneficiario(Afiliado beneficiario, Long id) {
		entity.createNativeQuery("insert into rel_afiliados_beneficiarios (estatus, fecha_creacion, id_beneficiario, id_afiliado) values (?, ?, ?, ?)")
			  .setParameter(1, true)
			  .setParameter(2, new Date())
			  .setParameter(3, beneficiario.getId())
			  .setParameter(4, id)
			  .executeUpdate();
	}
	
}
