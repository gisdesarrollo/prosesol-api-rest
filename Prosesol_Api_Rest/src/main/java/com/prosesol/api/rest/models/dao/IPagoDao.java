package com.prosesol.api.rest.models.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.prosesol.api.rest.models.entity.Pago;

public interface IPagoDao extends CrudRepository<Pago, Long>{

	@Modifying
	@Query("update Pago p set p.referenciaBancaria = :referencia, p.estatus = :estatus " +
			"where p.idTransaccion like :idTransaccion")
	public void actualizarEstatusPagoByIdTransaccion(@Param("referencia")String referencia,
									  @Param("estatus") String estatus,
									  @Param("idTransaccion")String nombreCompleto);

	@Query(nativeQuery = true, value = "select a.rfc from afiliados a, rel_afiliados_pagos rap, " +
			"pagos p where a.id_afiliado = rap.id_afiliado and rap.id_pago = p.id_pago " +
			"and p.id_transaccion like %?1%")
	public String getRfcByIdTransaccion(@Param("idTransaccion")String idTransaccion);
	
}
