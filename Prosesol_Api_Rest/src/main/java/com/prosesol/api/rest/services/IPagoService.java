package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.entity.Pago;

public interface IPagoService {

	public Pago save(Pago pago);

	public void actualizarEstatusPagoByIdTransaccion(String referencia, String estatus,
													 String idTransaccion);

	public String getRfcByIdTransaccion(String idTransaccion);
	
	public String getRfcCandidatoByIdTransaccion(String idTransaccion);
	
	public Pago getPagosByIdTransaccion(String idTransaccion);

}
