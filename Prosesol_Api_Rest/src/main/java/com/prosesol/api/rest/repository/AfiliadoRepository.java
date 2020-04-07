package com.prosesol.api.rest.repository;

import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Candidato;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Luis Enrique Morales Soriano
 */
@Repository
public class AfiliadoRepository {

    @PersistenceContext
    public EntityManager entityManager;

    @Transactional
    public void insertRelAfiliadosPagos(Afiliado afiliado, Long idPago){
        entityManager.createNativeQuery("insert into rel_afiliados_pagos values(?,?)")
                .setParameter(1, afiliado.getId())
                .setParameter(2, idPago)
                .executeUpdate();
    }
    
    @Transactional
    public void insertRelCandidatoPagos(Candidato candidato, Long idPago){
        entityManager.createNativeQuery("insert into rel_candidatos_pagos values(?,?)")
                .setParameter(1, candidato.getId())
                .setParameter(2, idPago)
                .executeUpdate();
    }
}
