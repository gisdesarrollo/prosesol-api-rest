package com.prosesol.api.rest.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.prosesol.api.rest.models.entity.Pago;

public interface IPagoDao extends CrudRepository<Pago, Long>{

	@Query("select p from Pago p where p.rfc = ?1")
	public List<Pago> getPagoByRfc(@Param("rfc") String rfc);

	@Modifying
	@Query("update Pago p set p.referenciaBancaria = :referencia, p.estatus = :estatus " +
			"where p.idTransaccion like :idTransaccion")
	public void actualizarEstatusPagoByIdTransaccion(@Param("referencia")String referencia,
									  @Param("estatus") String estatus,
									  @Param("idTransaccion")String nombreCompleto);

	@Query("select p from Pago p where p.idTransaccion like :idTransaccion")
	public Pago getRfcByIdTransaccion(@Param("idTransaccion")String idTransaccion);
	
}
