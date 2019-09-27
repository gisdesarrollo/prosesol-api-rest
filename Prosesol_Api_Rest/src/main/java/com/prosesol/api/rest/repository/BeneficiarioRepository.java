package com.prosesol.api.rest.repository;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.prosesol.api.rest.models.entity.Afiliado;



@Repository
public class BeneficiarioRepository {

	@PersistenceContext
	public EntityManager entity;
	
	@Transactional
	public void insertBeneficiario(Afiliado beneficiario, Long id) {
		entity.createNativeQuery("insert into rel_afiliados_beneficiarios (id_beneficiario, estatus, fecha_creacion, id_afiliado) values (?, ?, ?, ?)")
			  .setParameter(1, beneficiario.getId())
			  .setParameter(2, true)
			  .setParameter(3, new Date())
			  .setParameter(4, id)
			  .executeUpdate();
	}
	
}
