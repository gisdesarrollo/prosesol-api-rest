package com.prosesol.api.rest.services;

import java.util.List;

import com.prosesol.api.rest.models.entity.Pago;

public interface IPagoService {

	public List<Pago> findAll();

	public List<Pago> getPagoByRfc(String rfc);
	
	public Pago save(Pago pago);
	
	public void deleteById(Long id);
	
}
