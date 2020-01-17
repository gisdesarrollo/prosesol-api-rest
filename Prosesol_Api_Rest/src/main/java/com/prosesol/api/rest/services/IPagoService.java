package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.entity.Pago;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IPagoService {

	public List<Pago> findAll();

	public List<Pago> getPagoByRfc(String rfc);
	
	public Pago save(Pago pago);
	
	public void deleteById(Long id);

	public void actualizarEstatusPagoByIdTransaccion(String referencia, String estatus,
													 String idTransaccion);

	public Pago getRfcByIdTransaccion(String idTransaccion);
	
	
}
